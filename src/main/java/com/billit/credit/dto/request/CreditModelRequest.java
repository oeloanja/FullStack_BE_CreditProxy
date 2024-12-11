package com.billit.credit.dto.request;

import com.billit.credit.entity.MyData;
import com.billit.credit.entity.PdfData;
import com.billit.credit.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// request, response는 모델에 맞게 조정해야함
public class CreditModelRequest {
    private MyData myData;
    private Map<DocumentType, PdfData> pdfData;
    private BigDecimal loanAmount;
    private Integer term;
    private String purpose;
    private Integer accountBorrowId;
}

