package com.billit.credit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyDataResponse {
    private float intRate;
    private float installment;
    private int issueDPeriod;
    private float debt;
    private int crLinePeriod;
    private float pubRec;
    private float revolBal;
    private float revolUtil;
    private float openAcc;
    private float totalAcc;
    private float mortAcc;
    private float collections12MthsExMed;
    private int mortgageDebt;
    private int mortgageRepayment;
    private int repayment;
    private int mortgageTerm;
}
