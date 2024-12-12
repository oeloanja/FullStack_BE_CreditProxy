package com.billit.credit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreditModelRequest {
    private float int_rate;
    private float installment;
    private float dti;
    private float pub_rec;
    private float revol_bal;
    private float revol_util;
    private float open_acc;
    private float total_acc;
    private float mort_acc;
    private float collections_12_mths_ex_med;
    private float annual_inc;
    private float loan_amnt;

    private int cr_line_period;
    private int issue_d_period;
    private int emplength;
    private int loan_purpose;
}
