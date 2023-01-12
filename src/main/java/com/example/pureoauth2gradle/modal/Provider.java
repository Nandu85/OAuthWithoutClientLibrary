package com.example.pureoauth2gradle.modal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Provider {
    private String registration_id;
    private String client_id;
    private String client_secret;
    private String redirect_uri;
    private String scope;
    private String authUri;
    private String tokenUri;
    private String userInfoUri;
}
