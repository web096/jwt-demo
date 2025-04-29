package com.jwt.demo.common.auth;

import com.jwt.demo.common.exception.JwtException;
import com.jwt.demo.data.dto.JwtTokenDTO;
import com.jwt.demo.enums.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${security.jwt.token.key}")
    private String secretKey;

    // JWT 토큰 유효 시간 (5분)
    @Value("${security.jwt.token.expire}")
    private long validityInMilliseconds;

    // JWT refresh 토큰 유효 시간 default (1일)
    @Value("${security.jwt.token.refreshExpire}")
    private long refreshValidityInMilliseconds;

    public JwtTokenDTO generateToken(long userIdx, boolean isAutoLogin) {
        /*
        jjwt 0.11.0 부터
        java.security.Key를 사용하면 signWith(key, signatureAlgorithm)방식을 사용하지 못하고,
        signWith(key)로 가능하다.
        만약, signatureAlgorithm를 사용해서 암호화를 별도로 사용하고 싶다면,
        javax.crypto.SecretKey를 사용해서 key를 전달해야 된다.
        */
        SecretKey key = getSecretKey();

        String accessJti = UUID.randomUUID().toString();
        String refreshJti = UUID.randomUUID().toString();

        Date now = new Date();
        Date accessExpiry = new Date(now.getTime() + validityInMilliseconds);
        Date refreshExpiry = new Date(now.getTime() + refreshValidityInMilliseconds);

        // autoLogin일 경우, refreshToken의 만료시간을 30일로 변경
        if (isAutoLogin) {
            refreshExpiry = new Date(now.getTime() + (refreshValidityInMilliseconds * 30));
        }

        String accessToken = Jwts.builder()
                .claim("userIdx", userIdx)
                .claim("au", isAutoLogin)
                .claim("role", "USER")
                .id(accessJti)
                .issuedAt(now)
                .expiration(accessExpiry)
                .signWith(key, Jwts.SIG.HS512)
                .compact();

        String refreshToken = Jwts.builder()
                .claim("userIdx", userIdx)
                .claim("au", isAutoLogin)
                .claim("role", "USER")
                .id(refreshJti)
                .issuedAt(now)
                .expiration(refreshExpiry)
                .signWith(key, Jwts.SIG.HS512)
                .compact();

        return JwtTokenDTO.of(accessJti, accessToken, accessExpiry, refreshJti, refreshToken, refreshExpiry);
    }

    public TokenUser getUserFrom(String token) {
        Claims body = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return new TokenUser(body);
    }

    public TokenUser getTokenPayload(String token) {
        try {
            return getUserFrom(token);
        } catch (ExpiredJwtException e) {
            return new TokenUser(e.getClaims());
        } catch (io.jsonwebtoken.JwtException e) {
            throw new JwtException(ErrorCode.JWT_MALFORMED);
        }
    }

    // Jwt 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSecretKey()).build().parse(token);
            return true;
        } catch (MalformedJwtException ex) {
            throw new JwtException(ErrorCode.JWT_MALFORMED);
        } catch (SignatureException ex) {
            throw new JwtException(ErrorCode.JWT_SIGNATURE);
        } catch (ExpiredJwtException ex) {
            throw new JwtException(ErrorCode.JWT_EXPIRED);
        } catch (UnsupportedJwtException ex) {
            throw new JwtException(ErrorCode.JWT_UNSUPPORTED);
        } catch (IllegalArgumentException ex) {
            throw new JwtException(ErrorCode.JWT_ILLEGAL_ARGUMENT);
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parser().verifyWith(getSecretKey()).build().parse(token);
            return true;
        } catch (MalformedJwtException ex) {
            throw new JwtException(ErrorCode.JWT_MALFORMED);
        } catch (SignatureException ex) {
            throw new JwtException(ErrorCode.JWT_SIGNATURE);
        } catch (ExpiredJwtException ex) {
            throw new JwtException(ErrorCode.JWT_EXPIRED);
        } catch (UnsupportedJwtException ex) {
            throw new JwtException(ErrorCode.JWT_UNSUPPORTED);
        } catch (IllegalArgumentException ex) {
            throw new JwtException(ErrorCode.JWT_ILLEGAL_ARGUMENT);
        }
    }

    public String getToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring("Bearer ".length());
        }

        return null;
    }

    private SecretKey getSecretKey() {

        byte[] decodedKey = Base64.getDecoder().decode(secretKey);

        return Keys.hmacShaKeyFor(decodedKey);
    }
}
