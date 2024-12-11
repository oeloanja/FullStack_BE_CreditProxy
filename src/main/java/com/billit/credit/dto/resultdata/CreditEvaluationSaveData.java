package com.billit.credit.dto.resultdata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// request, response는 모델에 맞게 조정해야함
public class CreditEvaluationSaveData {
    private Long userBorrowId;
    private BigDecimal loanAmount;
    private Integer term;
    private String purpose;
    private BigDecimal creditScore;
    private BigDecimal interestRate;
}
