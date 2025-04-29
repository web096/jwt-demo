package com.jwt.demo.common.exception;


import com.jwt.demo.enums.ErrorCode;

public class JwtException extends BaseCustomException {

    public JwtException(ErrorCode errorCode) {
        super(errorCode);
    }
}
