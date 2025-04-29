package com.jwt.demo.common.response;


import com.jwt.demo.common.context.TraceContext;
import com.jwt.demo.enums.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ResultResponse {

    public <T> ResponseEntity<CommonBody<T>> success(T data) {
        return ResponseEntity.ok(wrap(200, "success", data));
    }

    public <T> ResponseEntity<CommonBody<Void>> success() {
        return ResponseEntity.ok(wrap(200, "success", null));
    }

    public <T> ResponseEntity<PageBody<T>> success(T data, Long page, Integer size, Long total) {
        return ResponseEntity.ok(wrapPage(200, "success", data, page, size, total));
    }

    public ResponseEntity<CommonBody<String>> error(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getErrorCode())
                .body(wrap(errorCode.getErrorCode(), errorCode.getErrorEngMessage(), null));
    }

    public ResponseEntity<CommonBody<String>> error(int code, String message) {
        return ResponseEntity.status(code).body(wrap(code, message, null));
    }

    public ResponseEntity<CommonBody<String>> error(String data, ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getErrorCode())
                .body(wrap(errorCode.getErrorCode(), errorCode.getErrorEngMessage(), data));
    }

    public <T> CommonBody<T> wrap(int code, String message, T data) {
        return CommonBody.<T>builder()
                .meta(CommonBody.Meta.builder()
                        .code(code)
                        .message(message)
                        .traceId(TraceContext.getTraceId())
                        .build())
                .data(data)
                .build();
    }

    public <T> PageBody<T> wrapPage(int code, String message, T data, Long page, int size, Long total) {
        return PageBody.<T>builder()
                .meta(PageBody.Meta.builder()
                        .code(code)
                        .message(message)
                        .traceId(TraceContext.getTraceId())
                        .build())
                .data(data)
                .page(page)
                .size(size)
                .total(total)
                .build();
    }
}
