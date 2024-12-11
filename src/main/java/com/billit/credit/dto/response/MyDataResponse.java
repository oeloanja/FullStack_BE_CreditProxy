package com.billit.credit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyDataResponse {
    private BigDecimal intRate;
    private BigDecimal installment;
    private String issueDPeriod;
    private Integer debt;
    private String crLinePeriod;
    private Integer pubRec;
    private Integer revolBal;
    private BigDecimal revolUtil;
    private Integer totalAcc;
    private Integer mortAcc;
    private BigDecimal collections12MthsExMed;
}
