package com.billit.credit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreditEvaluationRequest {
    private String phoneNumber;
    private String purpose;
    private BigDecimal amount;
    private Integer term;
}