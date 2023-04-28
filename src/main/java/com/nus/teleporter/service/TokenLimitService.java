package com.nus.teleporter.service;
import com.nus.teleporter.config.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

/***
 * TokenLimitService
 * Limit the same request
 */
@Service
@Slf4j
public class TokenLimitService implements LimitService {

    @Autowired
    private ServiceConfig serviceConfig;
    private StampedLock lock = new StampedLock();
    private ConcurrentReferenceHashMap map = new ConcurrentReferenceHashMap();
    private ScheduledExecutorService cleanScheduled = Executors.newScheduledThreadPool(100);

    /****
     * require
     * The same request(Client IP Address + userEmail) limit 3 times per 30 second by default.
     * return 0 means limited
     * @param times
     */
    @Override
    public int require(int hashCodeKey, int times) {
        long stamp = lock.writeLock();
        Integer ticket = 0;
        try {
            Object v = map.get(hashCodeKey);
            //start a new live cycle
            if((v == null)){
                cleanScheduled.schedule(new Runnable() {
                    @Override
                    public void run() {
                        log.info("clean all limited records, the life cycle restarts again.");
                        map.remove(hashCodeKey);
                    }
                }, serviceConfig.getGenerateTokenLimitExpire(),  TimeUnit.SECONDS);
                ticket = serviceConfig.getGenerateTokenLimitTimes() - times;
                map.put(hashCodeKey, ticket);
                return ticket;
            }
            //over limit time the request will be blocked.
            if(((Integer)v) <= 0)
                return 0;
            //sub a times
            ticket =(Integer) map.get(hashCodeKey);
            ticket = ticket  - times;
            map.put(hashCodeKey, ticket);
            return ticket;
        }finally {
            lock.unlockWrite(stamp);
        }
    }

    public int getKey(String ip, String plainText){
        return ip.concat(plainText).hashCode();
    }

}
