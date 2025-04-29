package com.jwt.demo.common.exception;


import com.jwt.demo.enums.ErrorCode;
import lombok.Getter;

@Getter
public class BaseCustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public BaseCustomException(ErrorCode errorCode) {
        super(errorCode.getErrorEngMessage());
        this.errorCode = errorCode;
    }
}
