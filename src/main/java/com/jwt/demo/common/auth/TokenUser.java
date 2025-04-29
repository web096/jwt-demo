package com.jwt.demo.common.auth;

import io.jsonwebtoken.Claims;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenUser {

    private Long userIdx;
    private boolean isAutoLogin;
    private String jti;
    private String role;

    public TokenUser(Claims claims) {
        this.userIdx = claims.get("userIdx", Long.class);
        this.isAutoLogin = claims.get("au", Boolean.class);
        this.jti = claims.getId();
        this.role = claims.get("role", String.class);
    }
}
