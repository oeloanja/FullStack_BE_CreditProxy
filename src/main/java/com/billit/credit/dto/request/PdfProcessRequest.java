package com.billit.credit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// request, response는 모델에 맞게 조정해야함
public class PdfProcessRequest {
    private String pdfUrl;
    private String documentType;
    private UUID userBorrowId;
}
