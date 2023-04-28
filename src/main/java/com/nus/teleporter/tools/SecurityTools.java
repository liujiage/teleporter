package com.nus.teleporter.tools;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SecurityTools {

    private SecurityTools(){}


    /****
     * Get client IP address
     * @param request
     * @return
     */
    public static String getIP(HttpServletRequest request){
        return request.getHeader("X-Forward-For") == null ? request.getRemoteAddr() : request.getHeader("X-Forward-For");
    }

}
