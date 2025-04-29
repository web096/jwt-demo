package com.jwt.demo.common.advice;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.jwt.demo.common.response.CommonBody;
import com.jwt.demo.common.response.PageBody;
import com.jwt.demo.common.response.ResultResponse;
import com.jwt.demo.enums.RtCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    private final ResultResponse resultResponse;

    private final ObjectMapper objectMapper;

    // 제외할 URI 패턴들
    private static final List<String> excludePaths = List.of(
            "/actuator",
            "/swagger",
            "/v3/api-docs",
            "/health"
    );

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // 모든 응답에 대해 적용
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        String path = request.getURI().getPath();

        // 요청 URI가 excludePaths에 포함되면 감싸지 않고 원본 반환
        if (excludePaths.stream().anyMatch(path::startsWith)) {
            return body;
        }

        // 이미 CommonBody로 래핑된 경우, 중복 래핑 방지
        if (body instanceof CommonBody) {
            return body;
        }

        // 이미 PageBody로 래핑된 경우, 중복 래핑 방지
        if (body instanceof PageBody) {
            return body;
        }

        if (body instanceof  ResponseEntity<?> responseEntity) {
            Object responseBody = responseEntity.getBody();
            HttpStatus status = HttpStatus.valueOf(responseEntity.getStatusCode().value());

            if (responseBody instanceof CommonBody) {
                return responseEntity;
            }

            CommonBody<Object> wrapped = resultResponse.wrap(RtCode.SUCCESS.getCode(), RtCode.SUCCESS.getDefaultEngMessage(), responseBody);

            return ResponseEntity
                    .status(status)
                    .headers(responseEntity.getHeaders())
                    .body(wrapped);
        }

        /*
        Spring 에서 @RestController 또는 @ResponseBody를 사용하는 경우,
        Java 객체(Object) -> JSON(String)으로 변환할 때는 HttpMessageConverter가 필요하다.
        String 타입은 HttpMessageConverter가 StringHttpMessageConverter로 따로 처리하는데,
        CommonResponseAdvice에서 CommonBody로 감싸기 때문에 return값이 String일 경우, 변환해야할 spring타입이 CommonBody라는 객체로 변경되어 있기 때문에 ClassCastException이 발생한다.
        String일 경우 따로 JSON으로 변환해주는 처리를 해준다.
        */
        if (body instanceof String) {
            try {
                CommonBody<Object> wrapped = resultResponse.wrap(RtCode.SUCCESS.getCode(), RtCode.SUCCESS.getDefaultEngMessage(), body);
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return objectMapper.writeValueAsString(wrapped);
            } catch (Exception e) {
                log.error("Error while converting response body to String", e);
            }
        }


        return resultResponse.wrap(RtCode.SUCCESS.getCode(), RtCode.SUCCESS.getDefaultEngMessage(), body);
    }
}
