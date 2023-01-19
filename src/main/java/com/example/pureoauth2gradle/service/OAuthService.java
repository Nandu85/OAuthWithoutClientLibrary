package com.example.pureoauth2gradle.service;

import com.example.pureoauth2gradle.modal.Provider;
import com.example.pureoauth2gradle.util.ProviderConstant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import netscape.javascript.JSObject;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.BasicHttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class OAuthService {

    RestTemplate restTemplate = new RestTemplate();

    private String verifier = null;
    private String challenge = null;

    public String getVerifier() {
        return verifier;
    }
    public void setVerifier(String verifier) {
        this.verifier = verifier;
    }
    public String getChallenge() {
        return challenge;
    }
    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public URI generateAuthCodeRequest(String registrationId) throws URISyntaxException {
        Provider provider = ProviderConstant.valueOf(registrationId).getDetail();
        Map<String, String> attributesMap = new HashMap<>();
        attributesMap.put("response_type", "code");
        attributesMap.put("client_id", provider.getClient_id());
        attributesMap.put("redirect_uri", provider.getRedirect_uri());
//        attributesMap.put("redirect_uri", Constants.GOOGLE_REDIRECT_URI);
//        attributesMap.put("scope", "openid profile email");
        attributesMap.put("scope", provider.getScope());

        URIBuilder uriBuilder = new URIBuilder(provider.getAuthUri());
//        URIBuilder uriBuilder = new URIBuilder("https://github.com/login/oauth/authorize");
//        URIBuilder uriBuilder = new URIBuilder("https://accounts.google.com/o/oauth2/v2/auth");
        attributesMap.entrySet()
                .forEach(stringEntry -> uriBuilder.addParameter(stringEntry.getKey(), stringEntry.getValue()));
        URI uri = uriBuilder.build();
        System.out.println(uri.toString());
        return uri;
    }

    public String generateAuthToken(String code, String registrationId) throws URISyntaxException {
        Provider provider = ProviderConstant.valueOf(registrationId).getDetail();
        Map<String, String> attributesMap = new HashMap<>();
        attributesMap.put("grant_type", "authorization_code");
        attributesMap.put("code", code);
        attributesMap.put("redirect_uri", provider.getRedirect_uri());
        attributesMap.put("client_id", provider.getClient_id());
        attributesMap.put("client_secret", provider.getClient_secret());

        URIBuilder uriBuilder = new URIBuilder(provider.getTokenUri());
        attributesMap.forEach(uriBuilder::addParameter);


        ResponseEntity<String> response = restTemplate.postForEntity(uriBuilder.build(), null, String.class);
        return response.getBody();
    }

    public String getPersonDetail(String token, String registrationId) throws URISyntaxException, IOException, ParseException {
        Provider provider = ProviderConstant.valueOf(registrationId).getDetail();
        URIBuilder uriBuilder = new URIBuilder(provider.getUserInfoUri());
        uriBuilder.addParameter("requestMask.includeField", "person.names,person.photos,person.email_addresses");
        URI uri = uriBuilder.build();
        HttpGet httpRequest = new HttpGet(uri);
        httpRequest.setHeader("Authorization", "Bearer " + token);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse = httpClient.execute(httpRequest);
        return EntityUtils.toString(httpResponse.getEntity());
    }

    public URI generateAuthCodePkceRequest(String registrationId) throws URISyntaxException, UnsupportedEncodingException, NoSuchAlgorithmException {
        SecureRandom sr = new SecureRandom();
        byte[] code = new byte[32];
        sr.nextBytes(code);
        setVerifier(Base64.getUrlEncoder().withoutPadding().encodeToString(code));

        byte[] bytes = verifier.getBytes("US-ASCII");
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(bytes, 0, bytes.length);
        byte[] digest = md.digest();
        setChallenge(org.apache.tomcat.util.codec.binary.Base64.encodeBase64URLSafeString(digest));

        Provider provider = ProviderConstant.valueOf(registrationId).getDetail();
        Map<String, String> attributesMap = new HashMap<>();
        attributesMap.put("response_type", "code");
        attributesMap.put("code_challenge", challenge);
        attributesMap.put("code_challenge_method", "S256");
        attributesMap.put("client_id", provider.getClient_id());
        attributesMap.put("redirect_uri", "http://localhost:8080/pkce");
        attributesMap.put("scope", provider.getScope());
        URIBuilder uriBuilder = new URIBuilder(provider.getAuthUri());
        attributesMap.forEach(uriBuilder::addParameter);
        URI uri = uriBuilder.build();
        System.out.println(uri.toString());
        return uri;
    }

    public String generateAuthPkceToken(String code) throws URISyntaxException, JsonProcessingException {
        Provider provider = ProviderConstant.valueOf("GOOGLE").getDetail();
        Map<String, String> attributesMap = new HashMap<>();
        attributesMap.put("grant_type", "authorization_code");
        attributesMap.put("client_id", provider.getClient_id());
        attributesMap.put("client_secret", provider.getClient_secret());
        attributesMap.put("code_verifier", getVerifier());
        attributesMap.put("code", code);
        attributesMap.put("redirect_uri", "http://localhost:8080/pkce");

        URI uriBuilder = new URIBuilder(provider.getTokenUri()).build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(attributesMap);

        HttpEntity<String> request = new HttpEntity<>(json, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(uriBuilder, request, String.class);
        return response.getBody();
    }
}
