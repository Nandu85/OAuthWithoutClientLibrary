package com.example.pureoauth2gradle.service;

import com.example.pureoauth2gradle.modal.Provider;
import com.example.pureoauth2gradle.util.Constants;
import com.example.pureoauth2gradle.util.ProviderConstant;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Service
public class OAuthService {

    RestTemplate restTemplate = new RestTemplate();

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
//        URIBuilder uriBuilder = new URIBuilder("https://github.com/login/oauth/access_token");
//        URIBuilder uriBuilder = new URIBuilder("https://oauth2.googleapis.com/token");
        attributesMap.forEach(uriBuilder::addParameter);


        ResponseEntity<String> response = restTemplate.postForEntity(uriBuilder.build(), null, String.class);
        return response.getBody();
    }

    public String getPersonDetail(String token, String registrationId) throws URISyntaxException, IOException, ParseException {
        Provider provider = ProviderConstant.valueOf(registrationId).getDetail();
        URIBuilder uriBuilder = new URIBuilder(provider.getUserInfoUri());
//        URIBuilder uriBuilder = new URIBuilder("https://api.github.com/user");
//        URIBuilder uriBuilder = new URIBuilder("https://www.googleapis.com/oauth2/v3/userinfo");
        uriBuilder.addParameter("requestMask.includeField", "person.names,person.photos,person.email_addresses");
        URI uri = uriBuilder.build();
        HttpGet httpRequest = new HttpGet(uri);
        httpRequest.setHeader("Authorization", "Bearer " + token);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse = httpClient.execute(httpRequest);
        return EntityUtils.toString(httpResponse.getEntity());
    }
}
