package com.billit.credit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_log")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserLog {

    @Id
    @Column(name = "user_pn", nullable = false)
    private String userPn;

    @Column(name = "int_rate", nullable = false)
    private Float intRate;

    @Column(name = "installment", nullable = false)
    private Float installment;

    @Column(name = "issue_d_period", nullable = false)
    private Float issueDPeriod;

    @Column(name = "debt", nullable = false)
    private Integer debt;

    @Column(name = "cr_line_period", nullable = false)
    private Float crLinePeriod;

    @Column(name = "pub_rec", nullable = false)
    private Float pubRec;

    @Column(name = "revol_bal", nullable = false)
    private Float revolBal;

    @Column(name = "revol_util", nullable = false)
    private Float revolUtil;

    @Column(name = "open_acc", nullable = false)
    private Float openAcc;

    @Column(name = "total_acc", nullable = false)
    private Float totalAcc;

    @Column(name = "mort_acc", nullable = false)
    private Float mortAcc;

    @Column(name = "collections_12_mths_ex_med", nullable = false)
    private Float collections12MthsExMed;

    @Column(name = "mortgage_debt", nullable = false)
    private Integer mortgageDebt;

    @Column(name = "mortgage_repayment", nullable = false)
    private Integer mortgageRepayment;

    @Column(name = "repayment", nullable = false)
    private Integer repayment;

    @Column(name = "mortgage_term", nullable = false)
    private Integer mortgageTerm;
}
