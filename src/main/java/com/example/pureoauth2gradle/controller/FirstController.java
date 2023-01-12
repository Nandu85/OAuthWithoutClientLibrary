package com.example.pureoauth2gradle.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@RestController
public class FirstController {

    @Autowired
    private com.example.pureoauth2gradle.service.OAuthService oAuthService;

    @GetMapping("/hello")
    public String hello() {
        return "Hello from spring !!!";
    }

    @GetMapping("/xyz")
    public String getResponse() {
        Authentication context = SecurityContextHolder.getContext().getAuthentication();
        String s = context.toString();
        return s;
    }

    @RequestMapping("/oauth2/authorization/{registrationId}")
    public void authorizedOAuth(HttpServletResponse response, @PathVariable String registrationId) throws URISyntaxException, IOException {
        response.sendRedirect(oAuthService.generateAuthCodeRequest(registrationId.toUpperCase()).toString());
    }

    @GetMapping("/token")
    public String authorizedOAuth2(@RequestParam("code") String code) throws URISyntaxException, IOException, ParseException {
        String response = oAuthService.generateAuthToken(code, "GOOGLE");
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String,String> responseMap = objectMapper.readValue(response, Map.class);
        String token = responseMap.get("access_token");
        String profileResponse = oAuthService.getPersonDetail(token, "GOOGLE");
        return "response " + profileResponse + "<br>code:  " + code + "<br>token is " + token;
    }

    @GetMapping("/login/oauth2/code/github")
    public String authorizedOAuth2Github(@RequestParam("code") String code) throws URISyntaxException, IOException, ParseException {
        String response = oAuthService.generateAuthToken(code, "GITHUB");
        Optional<String> token = Arrays.stream(response.split("&")).findFirst();    //.get("access_token");
        assert token.isPresent();
        String t = token.get();
        if (t.contains("access_token"))
            t = t.substring(t.indexOf('=')+1);
        String profileResponse = oAuthService.getPersonDetail(t, "GITHUB");
        return "response " + profileResponse + "<br>code:  " + code + "<br>token is " + token;
    }
}
