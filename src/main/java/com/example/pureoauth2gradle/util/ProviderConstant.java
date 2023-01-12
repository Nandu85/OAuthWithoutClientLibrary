package com.example.pureoauth2gradle.util;

import com.example.pureoauth2gradle.modal.Provider;

public enum ProviderConstant {

    GOOGLE {
        public Provider getDetail() {
            return new Provider(
                    "google",
                    "1062746014339-1ua6mgc2belnlo2k0r6opm40r3lm7748.apps.googleusercontent.com",
                    "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",
                    "http://localhost:8080/token",
                    "openid profile email",
                    "https://accounts.google.com/o/oauth2/v2/auth",
                    "https://oauth2.googleapis.com/token",
                    "https://www.googleapis.com/oauth2/v3/userinfo");
        }
    },
    GITHUB {
        public Provider getDetail() {
            return new Provider(
                    "github",
                    "2e8297e71753a5b6e127",
                    "d1c7576c312f51a003a0a46a3cad018cc5e49556",
                    "http://localhost:8080/login/oauth2/code/github",
                    "user:email,read:user",
                    "https://github.com/login/oauth/authorize",
                    "https://github.com/login/oauth/access_token",
                    "https://api.github.com/user");
        }
    };

    public abstract Provider getDetail();
}
