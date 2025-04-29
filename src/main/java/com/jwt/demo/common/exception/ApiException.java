package com.jwt.demo.common.exception;


import com.jwt.demo.enums.ErrorCode;

public class ApiException extends BaseCustomException {

    public ApiException(ErrorCode errorCode) {
        super(errorCode);
    }
}
