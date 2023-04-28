package com.nus.teleporter.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "teleporter")
@Getter
@Setter
public class ServiceConfig {
    private String userEmail;
    private String userPassword;
    private String cryptKey;
    private String crypOptionKey;
    private String cryptSalt;
    private Long tokenExpire;
    private Long generateTokenLimitExpire;
    private Integer generateTokenLimitTimes;

    //index mapping to index.html
    public static String PAGE_INDEX = "index";
    //success mapping to success.html
    public static String PAGE_SUCCESS = "success";
    //teleporter
    public static String PAGE_TELEPORTER = "teleporter";
}
