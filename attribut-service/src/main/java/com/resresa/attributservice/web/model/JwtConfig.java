package com.resresa.attributservice.web.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class JwtConfig {
    @Value("${security.jwt.uri}")
    private String Uri;

    @Value("${security.jwt.header}")
    private String header;

    @Value("${security.jwt.prefix}")
    private String prefix;

    @Value("${security.jwt.jwtExpirationInMs}")
    private int expiration;

    @Value("${security.jwt.secret}")
    private String secret;

    public String getUri() {
        return Uri;
    }

    public String getHeader() {
        return header;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getExpiration() {
        return expiration;
    }

    public String getSecret() {
        return secret;
    }
}

