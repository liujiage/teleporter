package com.nus.teleporter.tools;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommonTools {

    private CommonTools(){}

    public static String getCurrentTime(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
}
