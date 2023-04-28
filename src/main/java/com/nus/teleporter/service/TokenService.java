package com.nus.teleporter.service;

import com.google.gson.Gson;
import com.nus.teleporter.model.Token;

import java.util.UUID;

/*****
 * Teleporter SDK for managing token
 * generate a new token
 * validate check the token
 */
public interface TokenService  {
     Gson gson = new Gson();
     String generate(String plainText);
     String validate(String scrypToken, String value);

     default String tokenWrap(String plainText){
         return gson.toJson(Token.builder().
                 ts(String.valueOf(System.currentTimeMillis())).
                 uuid(String.valueOf(UUID.randomUUID())).plainText(plainText).build());
     }

     default Token tokenUnwrap(String token){
         return gson.fromJson(token, Token.class);
     }
}
