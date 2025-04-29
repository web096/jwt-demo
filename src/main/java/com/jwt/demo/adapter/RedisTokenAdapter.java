package com.jwt.demo.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisTokenAdapter {

    private final RedisTemplate<String, Object> redisTemplate;

    public void saveAccessToken(long userIdx, String jti, long expirationMillis) {
        redisTemplate.opsForValue().set(
                "jwt:access:" + userIdx + ":" + jti,
                "valid",
                java.time.Duration.ofMillis(expirationMillis)
        );
    }

    public void saveRefreshToken(long userIdx, String jti, long expirationMillis) {
        redisTemplate.opsForValue().set(
                "jwt:refresh:" + userIdx + ":" + jti,
                "valid",
                java.time.Duration.ofMillis(expirationMillis)
        );
    }

    public boolean isAccessTokenValid(long userIdx, String jti) {
        return redisTemplate.hasKey("jwt:access:" + userIdx + ":" + jti);
    }

    public boolean isRefreshTokenValid(long userIdx, String jti) {
        return redisTemplate.hasKey("jwt:refresh:" + userIdx + ":" + jti);
    }

    public void deleteTokens(long userIdx, String accessJti, String refreshJti) {
        redisTemplate.delete("jwt:access:" + userIdx + ":" + accessJti);
        redisTemplate.delete("jwt:refresh:" + userIdx + ":" + refreshJti);
    }
}
