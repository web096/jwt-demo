package com.jwt.demo.data.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OldTokenDTO {

    private long userIdx;

    private boolean isAutoLogin;

    private String accessTokenJti;

    private String refreshTokenJti;

    public static OldTokenDTO of(long userIdx, boolean isAutoLogin, String accessTokenJti, String refreshTokenJti) {
        OldTokenDTO oldTokenDTO = new OldTokenDTO();

        oldTokenDTO.userIdx = userIdx;
        oldTokenDTO.isAutoLogin = isAutoLogin;
        oldTokenDTO.accessTokenJti = accessTokenJti;
        oldTokenDTO.refreshTokenJti = refreshTokenJti;

        return oldTokenDTO;
    }
}
