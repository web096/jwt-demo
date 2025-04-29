package com.jwt.demo.service.auth.impl;

import com.jwt.demo.adapter.RedisTokenAdapter;
import com.jwt.demo.common.auth.JwtTokenProvider;
import com.jwt.demo.common.exception.ApiException;
import com.jwt.demo.common.exception.JwtException;
import com.jwt.demo.data.dto.JwtTokenDTO;
import com.jwt.demo.data.dto.LoginRequestDTO;
import com.jwt.demo.data.dto.LoginResponseDTO;
import com.jwt.demo.data.dto.OldTokenDTO;
import com.jwt.demo.enums.ErrorCode;
import com.jwt.demo.repository.user.UserRepository;
import com.jwt.demo.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private final RedisTokenAdapter redisTokenAdapter;

    @Override
    public LoginResponseDTO getToken(LoginRequestDTO loginRequestDTO) {

        var userEntity = userRepository.findByUserIdAndPassword(loginRequestDTO.getUserId(), loginRequestDTO.getPassword())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FIND));

        var jwtTokenDTO = jwtTokenProvider.generateToken(userEntity.getIdx(), loginRequestDTO.isAutoLogin());

        // Redis에 accessToken, refreshToken 저장
        saveRedisToken(userEntity.getIdx(), jwtTokenDTO);

        return LoginResponseDTO.from(jwtTokenDTO.getAccessToken(), jwtTokenDTO.getRefreshToken());
    }

    @Override
    public LoginResponseDTO reIssue(String bearerToken, String refreshToken) {

        // 검증
        var oldTokenDTO = validateToken(bearerToken, refreshToken);

        if (oldTokenDTO == null) {
            throw new JwtException(ErrorCode.JWT_MALFORMED);
        }

        // redis old jti 삭제
        redisTokenAdapter.deleteTokens(oldTokenDTO.getUserIdx(), oldTokenDTO.getAccessTokenJti(), oldTokenDTO.getRefreshTokenJti());

        var jwtTokenDTO = jwtTokenProvider.generateToken(oldTokenDTO.getUserIdx(), oldTokenDTO.isAutoLogin());

        // Redis에 accessToken, refreshToken 저장
        saveRedisToken(oldTokenDTO.getUserIdx(), jwtTokenDTO);

        return LoginResponseDTO.from(jwtTokenDTO.getAccessToken(), jwtTokenDTO.getRefreshToken());
    }

    @Override
    public void logout(String bearerToken, String refreshToken) {

        // 검증
        var oldTokenDTO = validateToken(bearerToken, refreshToken);

        if (oldTokenDTO == null) {
            throw new JwtException(ErrorCode.JWT_MALFORMED);
        }

        // redis old jti 삭제
        redisTokenAdapter.deleteTokens(oldTokenDTO.getUserIdx(), oldTokenDTO.getAccessTokenJti(), oldTokenDTO.getRefreshTokenJti());
    }

    private OldTokenDTO validateToken(String bearerToken, String refreshToken) {

        // accessToken 검증
        var oldAccessToken = jwtTokenProvider.getToken(bearerToken);

        var accessTokenUser = jwtTokenProvider.getTokenPayload(oldAccessToken);

        if (accessTokenUser.getUserIdx() == null || accessTokenUser.getJti() == null || accessTokenUser.getRole() == null) {
            throw new JwtException(ErrorCode.JWT_MALFORMED);
        }

        if (jwtTokenProvider.validateRefreshToken(refreshToken)) {

            var refreshTokenUser = jwtTokenProvider.getUserFrom(refreshToken);

            if (!redisTokenAdapter.isRefreshTokenValid(refreshTokenUser.getUserIdx(), refreshTokenUser.getJti())) {
                throw new JwtException(ErrorCode.JWT_UNAUTHORIZED);
            }

            return OldTokenDTO.of(refreshTokenUser.getUserIdx(), refreshTokenUser.isAutoLogin(), accessTokenUser.getJti(), refreshTokenUser.getJti());
        }

        return null;
    }

    private void saveRedisToken(long userIdx, JwtTokenDTO jwtTokenDTO) {
        //redis 저장
        redisTokenAdapter.saveAccessToken(
                userIdx,
                jwtTokenDTO.getAccessJti(),
                jwtTokenDTO.getAccessExpiry().getTime() - System.currentTimeMillis());

        redisTokenAdapter.saveRefreshToken(
                userIdx,
                jwtTokenDTO.getRefreshJti(),
                jwtTokenDTO.getRefreshExpiry().getTime() - System.currentTimeMillis());
    }
}
