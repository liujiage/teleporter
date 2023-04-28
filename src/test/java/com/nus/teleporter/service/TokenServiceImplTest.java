package com.nus.teleporter.service;

import com.nus.teleporter.config.ServiceConfig;
import com.nus.teleporter.model.StatusConst;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class TokenServiceImplTest {

    @Autowired
    ServiceConfig serviceConfig;
    @Autowired
    TokenService tokenService;

    @Test
    void generate_test(){
        String token = tokenService.generate(serviceConfig.getUserEmail());
        log.info(token);
    }

    @Test
    void validate_test(){
        String token = tokenService.generate(serviceConfig.getUserEmail());
        String res = tokenService.validate(token, serviceConfig.getUserEmail());
        Assertions.assertEquals(res, StatusConst.SUCCESS);
    }

    @Test
    void validate_error_token_incorrect_test(){
        String token = tokenService.generate(serviceConfig.getUserEmail());
        String res = tokenService.validate(token, "123@yahoo.com");
        Assertions.assertEquals(res, StatusConst.TOKEN_INCORRECT);
    }

    @Test
    void validate_error_token_expire_test() throws InterruptedException {
        String token = tokenService.generate(serviceConfig.getUserEmail());
        Thread.sleep(serviceConfig.getTokenExpire() * 1000L);
        Thread.yield();
        String res = tokenService.validate(token, serviceConfig.getUserEmail());
        Assertions.assertEquals(res, StatusConst.TOKEN_EXPIRE);
    }

}
