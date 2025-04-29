package com.jwt.demo.common.response;

import lombok.*;

/**
 * 성공 응답 body(paging navigate)
 * @param <T>
 */
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PageBody<T> {
    private Meta meta;
    private T data;
    private Long page;
    private int size;
    private Long total;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Meta {
        private final int code;
        private final String message;
        private final String traceId;
    }
}
