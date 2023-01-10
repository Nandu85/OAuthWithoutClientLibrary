package com.example.pureoauth2gradle.util;

public enum Provider {
    LOCAL("LOCAL"),
    GOOGLE("GOOGLE"),
    GITHUB("GITHUB"),
    FACEBOOK("FACEBOOK");

    final String clientName;
    Provider(String company){
        this.clientName = company;
    }
}
