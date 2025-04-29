package com.jwt.demo.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    DEFAULT(500, "서버에서 예상치 못한 오류가 발생했습니다.", "Internal Server Error"),

    JWT_AVAILABLE(200, "유효한 토큰입니다.", "This is a valid token."),
    JWT_UNAUTHORIZED(401, "인증되지 않은 접근입니다.", "Unauthorized"),
    JWT_MALFORMED(401,"올바르지 않은 토큰입니다.", "Invalid JWT token"),
    JWT_SIGNATURE(401,"토큰이 유효하지 않습니다.", "Token is invalid"),
    JWT_EXPIRED(401,"토큰이 만료되었습니다.", "Expired JWT token"),
    JWT_UNSUPPORTED(401,"지원되지 않은 토큰입니다.", "Unsupported JWT token"),
    JWT_ILLEGAL_ARGUMENT(400,"JWT 주요 문자열이 비었습니다.", "JWT claims string is empty"),

    USER_NOT_FIND(404, "회원정보를 찾을 수 없습니다.", "Membership information not found."),
    ;

    private final int errorCode;

    private final String errorKorMessage;

    private final String errorEngMessage;
}
