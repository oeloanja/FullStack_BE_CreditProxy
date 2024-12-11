package com.billit.credit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
// request, response는 모델에 맞게 조정해야함
public class CreditEvalResponse {
    private BigDecimal creditScore;
    private BigDecimal interestRate;
    private LocalDateTime evaluationDate;
}