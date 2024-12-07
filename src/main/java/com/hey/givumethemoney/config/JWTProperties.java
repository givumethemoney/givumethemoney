package com.hey.givumethemoney.Config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

// **
// *
// 이거 왠지 필요없는 파일같음
// **
@Component
@ConfigurationProperties(prefix = "spring.jwt")

public class JWTProperties {

    private String secret;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
