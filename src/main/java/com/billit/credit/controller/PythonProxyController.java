package com.billit.credit.controller;

import com.billit.credit.dto.request.CreditEvalRequest;
import com.billit.credit.dto.request.PdfProcessRequest;
import com.billit.credit.dto.response.ApiResponse;
import com.billit.credit.dto.response.CreditEvalResponse;
import com.billit.credit.dto.response.PdfProcessResponse;
import com.billit.credit.service.CreditEvaluationService;
import com.billit.credit.service.PdfProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/python-proxy")
@RequiredArgsConstructor
public class PythonProxyController {

    private final PdfProcessingService pdfService;
    private final CreditEvaluationService creditService;

    @PostMapping("/pdf/process")
    public ResponseEntity<ApiResponse<PdfProcessResponse>> processPdf(
            @RequestBody PdfProcessRequest request
    ) {
        PdfProcessResponse result = pdfService.processPdf(request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/credit/evaluate")
    public ResponseEntity<ApiResponse<CreditEvalResponse>> evaluateCredit(
            @RequestBody CreditEvalRequest request
    ) {
        CreditEvalResponse result = creditService.evaluateCredit(request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}