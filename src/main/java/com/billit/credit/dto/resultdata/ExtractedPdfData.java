package com.billit.credit.dto.resultdata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// request, response는 모델에 맞게 조정해야함
public class ExtractedPdfData {
    // 소득증명원
    private BigDecimal annualIncome;
    private String company;
    private LocalDate issuedDate;

    // 재직증명서
    private LocalDate joinDate;
    private String department;
    private String position;
}
