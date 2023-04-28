package com.nus.teleporter.controller;

import com.nus.teleporter.config.ServiceConfig;
import com.nus.teleporter.service.TokenLimitService;
import com.nus.teleporter.tools.SecurityTools;
import com.nus.teleporter.model.Login;
import com.nus.teleporter.model.StatusConst;
import com.nus.teleporter.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import static com.nus.teleporter.config.ServiceConfig.*;

/*****
 * DemoController
 * Teleporter demo rest API
 * The demo guide how to use Teleporter lib to validate Token.
 */
@Controller
@Slf4j
public class DemoController {

    @Autowired
    private ServiceConfig serviceConfig;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private TokenLimitService tokenLimitService;

    @GetMapping("/")
    public String index() {
        return PAGE_INDEX;
    }

    @GetMapping("/success")
    public String success() {
        return PAGE_SUCCESS;
    }

    /****
     * teleporter
     * Display teleporter input page when after scanned the QR code
     * Using Teleporter tokenService.generate() generate a token.
     * @param request
     * @param plainText
     * @param model
     * @return
     */
    @GetMapping("/teleporter")
    public String teleporter(HttpServletRequest request,
                             @RequestParam(value = "plainText", required = false) String plainText,
                             Model model){
        //Teleporter
        Login login = null;
        //Check input
        if(!StringUtils.hasText(plainText)) {
            login = new Login(plainText, "", "", StatusConst.PARAMETERS_ILLEGAL);
            model.addAttribute("login", login);
            return PAGE_TELEPORTER;
        }
        //Check token require limit
        int hashcodeKey =  SecurityTools.getIP(request).concat(plainText).hashCode();
        int ticket = tokenLimitService.require(hashcodeKey, 1 );
        if(ticket == 0){
            login = new Login(plainText, "", "", StatusConst.TOKEN_REQUIRE_OVER_LIMIT);
            model.addAttribute("login", login);
            return PAGE_TELEPORTER;
        }
        //Teleporter generate a token, the format is JSON that transfers from Token.class
        login = new Login(plainText, "", tokenService.generate(plainText), StatusConst.SUCCESS);
        model.addAttribute("login", login);
        return PAGE_TELEPORTER;
    }

    /******
     * login
     * User submit name and password login the system.
     * Using Teleporter tokenService.validate() validating the token.
     * @param request
     * @param login
     * @param model
     * @return
     */
    @PostMapping(value="/login")
    public String login(HttpServletRequest request,
                        @ModelAttribute Login login, Model model) {
        //Check input
        if(!StringUtils.hasText(login.getPlainText()) ||
                !StringUtils.hasText(login.getPassword())){
            login.setStatus(StatusConst.PARAMETERS_ILLEGAL);
            model.addAttribute("login", login);
            return PAGE_TELEPORTER;
        }
        //Check token require limit
        int hashcodeKey =  SecurityTools.getIP(request).concat(login.getPlainText()).hashCode();
        int ticket = tokenLimitService.require(hashcodeKey, 1 );
        if(ticket == 0){
            login = new Login(login.getPlainText(), "", "", StatusConst.TOKEN_REQUIRE_OVER_LIMIT);
            model.addAttribute("login", login);
            return PAGE_TELEPORTER;
        }
        //Teleporter validate token
        String result = tokenService.validate(login.getToken(), login.getPlainText());
        login.setStatus(result);
        model.addAttribute("login", login);
        if(result.equals(StatusConst.SUCCESS)){
            //Check the user whether exists in the system.
            //for testing only has one user is correct.
            //User's email: test@yahoo.com Password: 123456
            if(!serviceConfig.getUserEmail().equals(login.getPlainText()) ||
            !serviceConfig.getUserPassword().equals(login.getPassword())){
                login.setStatus(StatusConst.USERNAME_OR_PASSWORD_INCORRECT);
                //go back to teleporter page
                log.info("Login failed!");
                return PAGE_TELEPORTER;
            }
            //go to successful page
            log.info("Login success!");
            return PAGE_SUCCESS;
        }
        return PAGE_TELEPORTER;
    }

}
