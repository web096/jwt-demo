package com.jwt.demo.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CommonBody<T> {

    private final Meta meta;
    private final T data;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Meta {
        private final int code;
        private final String message;
        private final String traceId;
    }
}
