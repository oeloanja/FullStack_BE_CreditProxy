package com.billit.credit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Common Errors
    INVALID_INPUT_VALUE(400, "C001", "입력값이 잘못되었습니다."),
    INTERNAL_SERVER_ERROR(500, "C002", "서버 에러가 발생했습니다."),

    // PDF Related Errors
    PDF_UPLOAD_ERROR(500, "P001", "PDF 업로드 중 오류가 발생했습니다"),
    PDF_PROCESSING_ERROR(500, "P002", "PDF 처리 중 오류가 발생했습니다"),
    PDF_NOT_FOUND(404, "P003", "PDF 데이터를 찾을 수 없습니다"),

    // Credit Evaluation Errors
    CREDIT_EVAL_ERROR(500, "E001", "신용평가 중 오류가 발생했습니다"),
    MYDATA_NOT_FOUND(404, "E002", "마이데이터를 찾을 수 없습니다"),
    INSUFFICIENT_DATA(400, "E003", "신용평가에 필요한 데이터가 부족합니다");

    private final int status;
    private final String code;
    private final String message;
}
