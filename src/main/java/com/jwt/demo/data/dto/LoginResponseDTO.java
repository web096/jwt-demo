package com.jwt.demo.data.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {

    private String accessToken;

    private String refreshToken;

    public static LoginResponseDTO from(String accessToken, String refreshToken) {
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();

        loginResponseDTO.accessToken = accessToken;
        loginResponseDTO.refreshToken = refreshToken;

        return loginResponseDTO;
    }
}
