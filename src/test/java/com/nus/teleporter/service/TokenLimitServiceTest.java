package com.nus.teleporter.service;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.nus.teleporter.tools.CommonTools.getCurrentTime;

@SpringBootTest
@Slf4j
public class TokenLimitServiceTest {

    @Autowired
    TokenLimitService tokenLimitService;

    /*****
     * require_test
     * Simulation user over times require token, will be blocked.
     * @throws InterruptedException
     */
    @Test
    public void require_test() throws InterruptedException {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //the same request require 10 times
                for(int i=0; i<10; i++) {
                    //get hashcode
                    int hashcodeKey = tokenLimitService.getKey("127.0.0.1", "1234@yahoo.com");
                    int ticket = tokenLimitService.require(hashcodeKey, 1);
                    log.info("Reqeust time: {} request times: {} hashcodeKey: {} ticket: {}",getCurrentTime(),i, hashcodeKey,ticket);
                    //the user will be blocked if user require over 3 times during per 30 second
                    if(ticket == 0){
                        log.info("--------user's reqeust blocked! after 30 second will be unblock! ");
                        try {
                            log.info("--------simulation process other user's reqeust, the thread sleep 10000 millis.");
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });
        thread.join();
        thread.run();
        Thread.yield();
    }
}
