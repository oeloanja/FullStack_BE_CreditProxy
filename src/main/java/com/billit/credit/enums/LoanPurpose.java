package com.billit.credit.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum LoanPurpose {
    DEBT_CONSOLIDATION(0, "채무정리"),
    CREDIT_CARD(1, "신용카드"),
    HOME_IMPROVEMENT(2, "주택개선(리모델링)"),
    DEBT_REFINANCING(3, "대환 구매"),
    MEDICAL(4, "의료"),
    BUSINESS(5, "사업"),
    VEHICLE(6, "차량 구매"),
    VACATION(7, "휴가"),
    RELOCATION(8, "이주"),
    HOME_BUYING(9, "집 구매"),
    WEDDING(10, "결혼"),
    EDUCATION(11, "교육"),
    OTHER(12, "기타");

    private final int code;
    private final String description;

    LoanPurpose(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public int getCode() {
        return code;
    }

    public static LoanPurpose fromString(String description) {
        for (LoanPurpose purpose : LoanPurpose.values()) {
            if (purpose.getDescription().equals(description)) {
                return purpose;
            }
        }
        throw new IllegalArgumentException("Unknown loan purpose: " + description);
    }

    public static LoanPurpose fromCode(int code) {
        for (LoanPurpose purpose : LoanPurpose.values()) {
            if (purpose.getCode() == code) {
                return purpose;
            }
        }
        throw new IllegalArgumentException("Unknown loan purpose code: " + code);
    }
}
