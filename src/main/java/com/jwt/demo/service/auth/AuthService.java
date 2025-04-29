package com.jwt.demo.service.auth;

import com.jwt.demo.data.dto.LoginRequestDTO;
import com.jwt.demo.data.dto.LoginResponseDTO;

public interface AuthService {

    public LoginResponseDTO getToken(LoginRequestDTO loginRequestDTO);

    public LoginResponseDTO reIssue(String bearerToken, String refreshToken);

    public void logout(String bearerToken, String refreshToken);
}
