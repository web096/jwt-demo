package com.jwt.demo.data.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class JwtTokenDTO {

    private String accessJti;
    private String accessToken;
    private Date accessExpiry;
    private String refreshJti;
    private String refreshToken;
    private Date refreshExpiry;

    public static JwtTokenDTO of(String accessJti, String accessToken, Date accessExpiry, String refreshJti, String refreshToken, Date refreshExpiry) {
        JwtTokenDTO jwtTokenDTO = new JwtTokenDTO();

        jwtTokenDTO.accessJti = accessJti;
        jwtTokenDTO.accessToken = accessToken;
        jwtTokenDTO.accessExpiry = accessExpiry;
        jwtTokenDTO.refreshJti = refreshJti;
        jwtTokenDTO.refreshToken = refreshToken;
        jwtTokenDTO.refreshExpiry = refreshExpiry;

        return jwtTokenDTO;
    }
}
