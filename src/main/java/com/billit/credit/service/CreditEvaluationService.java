package com.billit.credit.service;

import com.billit.credit.cache.CacheData;
//import com.billit.credit.client.CreditModelClient;
import com.billit.credit.client.PdfExtractorClient;
//import com.billit.credit.dto.DocumentData;
//import com.billit.credit.dto.request.CreditEvaluationRequest;
//import com.billit.credit.dto.request.CreditModelRequest;
import com.billit.credit.dto.EmploymentCertificateData;
import com.billit.credit.dto.IncomeProofData;
import com.billit.credit.dto.request.DocumentUrlRequest;
//import com.billit.credit.dto.response.CreditEvaluationResponse;
//import com.billit.credit.dto.response.MyDataResponse;
import com.billit.credit.dto.response.DocumentExtractResponse;
import com.billit.credit.enums.DocumentType;
import lombok.RequiredArgsConstructor;
//import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CreditEvaluationService {
//    private final JdbcTemplate jdbcTemplate;
    private final PdfExtractorClient pdfExtractorClient;
//    private final CreditModelClient creditModelClient;

    private final Map<String, CacheData> documentDataCache = new HashMap<>();

    @Scheduled(fixedRate = 1800000)
    public void cleanupCache() {
        long currentTime = System.currentTimeMillis();
        long timeoutMillis = 1800000;
        documentDataCache.entrySet().removeIf(entry ->
                currentTime - entry.getValue().getTimestamp() > timeoutMillis);
    }


//    public MyDataResponse getMyDataByPhoneNumber(String phoneNumber) {
//        String sql = """
//            SELECT int_rate, installment, issue_d_period, debt, cr_line_period,
//                   pub_rec, revol_bal, revol_util, total_acc, mort_acc,
//                   collections_12_mths_ex_med
//            FROM my_data
//            WHERE phone_number = ?
//        """;
//
//        return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
//                        new MyDataResponse(
//                                rs.getBigDecimal("int_rate"),
//                                rs.getBigDecimal("installment"),
//                                rs.getString("issue_d_period"),
//                                rs.getInt("debt"),
//                                rs.getString("cr_line_period"),
//                                rs.getInt("pub_rec"),
//                                rs.getInt("revol_bal"),
//                                rs.getBigDecimal("revol_util"),
//                                rs.getInt("total_acc"),
//                                rs.getInt("mort_acc"),
//                                rs.getBigDecimal("collections_12_mths_ex_med")
//                        ),
//                phoneNumber
//        );
//    }

    // 소득증명원에서 뽑아온 값 캐싱
    public DocumentExtractResponse<IncomeProofData> processIncomeProof(DocumentUrlRequest request) {
        DocumentExtractResponse<IncomeProofData> response = pdfExtractorClient.extractIncomeProofData(request.getFileUrl());

        CacheData cacheData = documentDataCache.getOrDefault(
                request.getPhoneNumber(),
                new CacheData(new HashMap<>(), System.currentTimeMillis())
        );
        cacheData.getData().put(DocumentType.INCOME_PROOF, response);
        cacheData.setTimestamp(System.currentTimeMillis());
        documentDataCache.put(request.getPhoneNumber(), cacheData);

        return response;
    }

    public DocumentExtractResponse<EmploymentCertificateData> processEmploymentCertificate(DocumentUrlRequest request) {
        DocumentExtractResponse<EmploymentCertificateData> response = pdfExtractorClient.extractEmploymentCertificateData(request.getFileUrl());

        CacheData cacheData = documentDataCache.getOrDefault(
                request.getPhoneNumber(),
                new CacheData(new HashMap<>(), System.currentTimeMillis())
        );
        cacheData.getData().put(DocumentType.EMPLOYMENT_CERTIFICATE, response);
        cacheData.setTimestamp(System.currentTimeMillis());
        documentDataCache.put(request.getPhoneNumber(), cacheData);

        return response;
    }

//    public CreditEvaluationResponse evaluateCredit(CreditEvaluationRequest request) {
//        // 마이데이터
////        MyDataResponse myData = getMyDataByPhoneNumber(request.getPhoneNumber());
//
//        // pdf 캐싱해둔거
//        CacheData cacheData = documentDataCache.get(request.getPhoneNumber());
//        if (cacheData == null) {
//            throw new RuntimeException("No cached document data found");
//        }
//
//        Map<DocumentType, DocumentExtractResponse> documents = cacheData.getData();
//        if (!documents.containsKey(DocumentType.INCOME_PROOF)
//            || !documents.containsKey(DocumentType.EMPLOYMENT_CERTIFICATE)) {
//            throw new RuntimeException("Required documents (Income Proof and Employment Certificate) must be processed first");
//        }
//
//
//        DocumentData incomeProofData = documents.get(DocumentType.INCOME_PROOF).getData();
//        DocumentData employmentData = documents.get(DocumentType.EMPLOYMENT_CERTIFICATE).getData();
//
//        // 신용평가 모델 요청 데이터 구성
//        CreditModelRequest modelRequest = CreditModelRequest.builder()
//
//                .intRate(myData.getIntRate())
//                .installment(myData.getInstallment())
//                .issueDPeriod(myData.getIssueDPeriod())
//                .debt(myData.getDebt())
//                .crLinePeriod(myData.getCrLinePeriod())
//                .pubRec(myData.getPubRec())
//                .revolBal(myData.getRevolBal())
//                .revolUtil(myData.getRevolUtil())
//                .totalAcc(myData.getTotalAcc())
//                .mortAcc(myData.getMortAcc())
//                .collections12MthsExMed(myData.getCollections12MthsExMed())
//
//                .continuousYear(employmentData.getContinuousYear())
//
//                .income(incomeProofData.getIncome())
//
//                .purpose(LoanPurpose.fromString(request.getPurpose()).getCode())
//                .requestAmount(request.getAmount())
//                .requestTerm(request.getTerm())
//                .build();
//
//        // 신용평가 모델 호출
//        CreditEvaluationResponse response = creditModelClient.evaluateCredit(modelRequest);
//
//        documentDataCache.remove(request.getPhoneNumber());
//        return response;
//    }
}