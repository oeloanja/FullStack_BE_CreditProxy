package com.billit.credit.controller;

import com.billit.credit.dto.EmploymentCertificateData;
import com.billit.credit.dto.IncomeProofData;
import com.billit.credit.dto.request.DocumentUrlRequest;
import com.billit.credit.dto.response.DocumentExtractResponse;
import com.billit.credit.service.CreditEvaluationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/credit")
@RequiredArgsConstructor
public class CreditEvaluationController {
    private final CreditEvaluationService creditEvaluationService;

//    @GetMapping("/mydata")
//    public ResponseEntity<MyDataResponse> getMyData(@RequestBody String phoneNumber) {
//        return ResponseEntity.ok(creditEvaluationService.getMyDataByPhoneNumber(phoneNumber));
//    }

    @PostMapping("/document/income-proof")
    public ResponseEntity<DocumentExtractResponse<IncomeProofData>> processIncomeProof(@RequestBody DocumentUrlRequest request) {
        log.info("소득증명원 요청 URL: {}", request.getFileUrl());
        DocumentExtractResponse<IncomeProofData> response = creditEvaluationService.processIncomeProof(request);
        log.info("추출한 연 소득: {}", response.getData().getIncome());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/document/employment-certificate")
    public ResponseEntity<DocumentExtractResponse<EmploymentCertificateData>> processEmploymentCertificate(@RequestBody DocumentUrlRequest request) {
        log.info("재직증명서 요청 URL: {}", request.getFileUrl());
        DocumentExtractResponse<EmploymentCertificateData> response = creditEvaluationService.processEmploymentCertificate(request);
        log.info("추출한 근속 기간: {}", response.getData().getEmp_length());
        return ResponseEntity.ok(response);
    }

//    @PostMapping("/evaluate")
//    public ResponseEntity<CreditEvaluationResponse> evaluateCredit(@RequestBody CreditEvaluationRequest request) {
//        return ResponseEntity.ok(creditEvaluationService.evaluateCredit(request));
//    }
}
