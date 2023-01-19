package com.example.pureoauth2gradle.controller;

import com.example.pureoauth2gradle.service.OAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@RestController
public class PkceController {

    @Autowired
    private OAuthService oAuthService;

    @GetMapping("/oauth2/authorization/google/pkce")
    public void getCode(HttpServletResponse response) throws URISyntaxException, IOException, NoSuchAlgorithmException {
        response.sendRedirect(oAuthService.generateAuthCodePkceRequest("GOOGLE").toString());
    }

    @GetMapping("/pkce")
    public String getPkce(@RequestParam("code") String code) throws URISyntaxException, IOException, ParseException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> responseMap = objectMapper.readValue(oAuthService.generateAuthPkceToken(code), Map.class);
        String token = responseMap.get("access_token");
        return oAuthService.getPersonDetail(token, "GOOGLE");
    }

//    @GetMapping("/pkce/token")
//    public String getPkceToken(TokenResponse response) throws IOException, URISyntaxException, ParseException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        Map<String,String> responseMap = objectMapper.readValue(response.toString(), Map.class);
//        String token = responseMap.get("access_token");
//        return oAuthService.getPersonDetail(token, "GOOGLE");
//    }
}
