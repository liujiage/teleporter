package com.nus.teleporter.service;

import com.google.gson.Gson;
import com.nus.teleporter.model.Token;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

@SpringBootTest
@Slf4j
public class CryptServiceTests {

    Gson gson = new Gson();
    String email = "test@yahoo.com";

    @Test
    void crypt_test()
            throws InvalidKeySpecException, NoSuchAlgorithmException,
            IllegalBlockSizeException, InvalidKeyException, BadPaddingException,
            InvalidAlgorithmParameterException, NoSuchPaddingException {
        String plainText = email;
        String password = "teleporter";
        String salt = "1q2w3e4r%";
        String optionKey = "aqswxdERFtgedRfe";
        IvParameterSpec ivParameterSpec = new IvParameterSpec(optionKey.getBytes());
        SecretKey key = CryptService.getKey(password,salt);
        String cipherText = CryptService.encrypt(plainText, key, ivParameterSpec);
        String decryptedCipherText = CryptService.decrypt(
                cipherText, key, ivParameterSpec);
        Assertions.assertEquals(plainText, decryptedCipherText);
    }

    @Test
    void token_to_json(){
        Token token = Token.builder().
                ts(String.valueOf(System.currentTimeMillis())).
                uuid(String.valueOf(UUID.randomUUID())).plainText(email).build();
        String json = gson.toJson(token);
        log.info(json);
        Token token2 = gson.fromJson(json, Token.class);
        log.info(token2.toString());
    }
}


