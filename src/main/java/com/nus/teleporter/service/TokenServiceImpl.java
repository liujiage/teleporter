package com.nus.teleporter.service;

import com.nus.teleporter.config.ServiceConfig;
import com.nus.teleporter.model.StatusConst;
import com.nus.teleporter.model.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.spec.IvParameterSpec;

@Service
@Slf4j
public class TokenServiceImpl implements TokenService {

    @Autowired
    private ServiceConfig serviceConfig;

    /****
     * Validate the token
     * 1) Check the token whether expire or not
     * 2) Check the token whether correct
     * @param scrypToken
     * @param value
     * @return
     */
    @Override
    public String validate(String scrypToken, String value){
        if(!StringUtils.hasText(scrypToken))
            return StatusConst.TOKEN_FAILED;
        else if(!StringUtils.hasText(value))
            return StatusConst.PARAMETERS_ILLEGAL;
        String plainText = null;
        try {
            //encode the token
            IvParameterSpec ivParameterSpec =
                    new IvParameterSpec(serviceConfig.getCrypOptionKey().getBytes());
            plainText = CryptService.decrypt(scrypToken,
                    CryptService.getKey(serviceConfig.getCryptKey(), serviceConfig.getCryptSalt()),
                    ivParameterSpec);
            //get the token model
            Token token = this.tokenUnwrap(plainText);
            //1) Check the token whether expire or not
            long issueTs = Long.parseLong(token.getTs());
            long currentTs = System.currentTimeMillis();
            //Millisecond = Second * 1000
            long expireTs = serviceConfig.getTokenExpire() * 1000L;
            if((currentTs - issueTs) > expireTs){
                log.error("The token has been expire! issueTs: {} currentTs: {} expireTs: {} ",
                        issueTs,currentTs, expireTs);
                return StatusConst.TOKEN_EXPIRE;
            }
            //2) Check the token whether correct
            if(!token.getPlainText().equals(value)){
                log.error("The token has been expire! issueTs: {} currentTs: {} expireTs: {} ",
                        issueTs,currentTs, expireTs);
                return StatusConst.TOKEN_INCORRECT;
            }
        }catch(Exception e){
            log.error("generate token error scrypToken: {} plainText: {}", scrypToken,plainText,  e);
            return StatusConst.TOKEN_FAILED;
        }
        return StatusConst.SUCCESS;
    }

    /****
     * Generate a new token
     * @param plainText
     * @return
     */
    @Override
    public String generate(String plainText) {
        try {
            if(!StringUtils.hasText(plainText))
                return null;
            IvParameterSpec ivParameterSpec =
                    new IvParameterSpec(serviceConfig.getCrypOptionKey().getBytes());
            return CryptService.encrypt(
                    this.tokenWrap(plainText),
                    CryptService.getKey(serviceConfig.getCryptKey(), serviceConfig.getCryptSalt()),
                    ivParameterSpec);
        }catch(Exception e){
            log.error("generate token error plainText: {}", plainText, e);
        }
        return null;
    }
}
