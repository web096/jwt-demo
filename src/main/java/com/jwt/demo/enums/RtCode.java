package com.jwt.demo.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RtCode {

    SUCCESS(200, "success", "성공"),
    ERROR(0, "error", "에러")
    ;

    private final int code;

    private final String defaultEngMessage;

    private final String defaultKorMessage;
}
