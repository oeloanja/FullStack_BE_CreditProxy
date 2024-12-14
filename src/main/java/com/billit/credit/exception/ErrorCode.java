package com.billit.credit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    DOCUMENT_NOT_FOUND(1000, "필요한 문서를 찾을 수 없습니다."),
    DOCUMENT_PROCESSING_FAILED(1001, "문서 처리 중 오류가 발생했습니다."),
    REQUIRED_DOCUMENTS_MISSING(1002, "소득증명원과 재직증명서가 모두 필요합니다."),

    CREDIT_EVALUATION_FAILED(2000, "신용평가 처리 중 오류가 발생했습니다."),
    INVALID_LOAN_PURPOSE(2001, "유효하지 않은 대출 목적입니다."),

    MYDATA_NOT_FOUND(3000, "마이데이터 정보를 찾을 수 없습니다."),
    MYDATA_PROCESSING_ERROR(3001, "마이데이터 처리 중 오류가 발생했습니다."),

    USER_UPDATE_FAILED(4000, "사용자 정보 업데이트에 실패했습니다."),

    INTERNAL_SERVER_ERROR(9000, "내부 서버 오류가 발생했습니다.");

    private final int code;
    private final String message;
}
