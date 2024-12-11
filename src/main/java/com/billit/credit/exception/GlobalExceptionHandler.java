package com.billit.credit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<com.billit.credit.exception.ErrorResponse> handleBusinessException(BusinessException e) {
        log.error("Business Exception: {}", e.getErrorCode());
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(new ErrorResponse(e.getErrorCode()));
    }

    @ExceptionHandler(WebClientResponseException.class)
    protected ResponseEntity<ErrorResponse> handleWebClientResponseException(WebClientResponseException e) {
        log.error("External Service Error: {}", e.getMessage());
        return ResponseEntity
                .status(e.getStatusCode())
                .body(new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Internal Server Error: ", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
