package com.billit.credit.exception;

import lombok.Getter;

@Getter
public class CreditServiceException extends RuntimeException {
    private final ErrorCode errorCode;

    public CreditServiceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CreditServiceException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }

    public CreditServiceException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}
