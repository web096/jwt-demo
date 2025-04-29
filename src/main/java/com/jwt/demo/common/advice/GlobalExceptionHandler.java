package com.jwt.demo.common.advice;


import com.jwt.demo.common.exception.ApiException;
import com.jwt.demo.common.exception.JwtException;
import com.jwt.demo.common.response.ResultResponse;
import com.jwt.demo.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ResultResponse resultResponse;

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<?> handleJwtExceptionn(JwtException e) {
        log.error("JwtException: {}", e.getMessage(), e);
        return resultResponse.error(e.getErrorCode());
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApiException(ApiException e) {
        log.error("ApiException: {}", e.getMessage(), e);
        return resultResponse.error(e.getErrorCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error("Exception: {}", e.getMessage(), e);
        return resultResponse.error(ErrorCode.DEFAULT);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNoResourceException(NoResourceFoundException e) {
        log.error("No Resource Found Exception: {}", e.getMessage(), e);
        return resultResponse.error(e.getStatusCode().value(), e.getLocalizedMessage());
    }
}
