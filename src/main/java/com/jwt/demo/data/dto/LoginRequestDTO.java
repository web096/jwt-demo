package com.jwt.demo.data.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {

    private String userId;

    private String password;

    private boolean isAutoLogin;
}
