package com.example.pureoauth2gradle.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

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

    @RequestMapping("/oauth2/authorization/google")
    public void authorizedOAuth(HttpServletResponse response) throws URISyntaxException, IOException {
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<String> s = restTemplate.postForEntity(oAuthService.generateAuthCodeRequest(), null, String.class);
//        System.out.println(s);
//        return s.getBody();
        response.sendRedirect(oAuthService.generateAuthCodeRequest().toString());
    }

    @GetMapping("/token")
    public String authorizedOAuth2(@RequestParam("code") String code) throws URISyntaxException, IOException, ParseException {
        String response = oAuthService.generateAuthToken(code);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String,String> responseMap = objectMapper.readValue(response, Map.class);
        String token = responseMap.get("access_token");
        String profileResponse = oAuthService.getPersonDetail(token);
        return "response " + profileResponse + "<br>code:  " + code + "<br>token is " + token;
    }
}
