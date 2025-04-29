package com.jwt.demo.controller.auth;

import com.jwt.demo.common.response.CommonBody;
import com.jwt.demo.common.response.ResultResponse;
import com.jwt.demo.data.dto.LoginRequestDTO;
import com.jwt.demo.data.dto.LoginResponseDTO;
import com.jwt.demo.data.dto.RefreshTokenRequestDTO;
import com.jwt.demo.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthService authService;

    private final ResultResponse resultResponse;

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return authService.getToken(loginRequestDTO);
    }

    @PostMapping("/reissue")
    public LoginResponseDTO reIssue(@Parameter(hidden = true) @RequestHeader("Authorization") String bearerToken,
                                    @RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {

        return authService.reIssue(bearerToken, refreshTokenRequestDTO.getRefreshToken());
    }

    @PostMapping("/logout")
    public ResponseEntity<CommonBody<Void>> logout(@Parameter(hidden = true) @RequestHeader("Authorization") String bearerToken,
                                             @RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {

        authService.logout(bearerToken, refreshTokenRequestDTO.getRefreshToken());

        return resultResponse.success();
    }
}
