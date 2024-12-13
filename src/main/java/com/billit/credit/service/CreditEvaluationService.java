package com.billit.credit.service;

import com.billit.credit.cache.CacheData;
import com.billit.credit.client.CreditModelClient;
import com.billit.credit.client.PdfExtractorClient;
import com.billit.credit.client.UserServiceClient;
import com.billit.credit.dto.EmploymentCertificateData;
import com.billit.credit.dto.IncomeProofData;
import com.billit.credit.dto.request.CreditEvaluationRequest;
import com.billit.credit.dto.request.CreditModelRequest;
import com.billit.credit.dto.request.DocumentUrlRequest;
import com.billit.credit.dto.request.UserCreditUpdateRequest;
import com.billit.credit.dto.response.CreditEvaluationResponse;
import com.billit.credit.dto.response.DocumentExtractResponse;
import com.billit.credit.dto.response.MyDataResponse;
import com.billit.credit.enums.DocumentType;
import com.billit.credit.enums.LoanPurpose;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditEvaluationService {
    private final JdbcTemplate jdbcTemplate;
    private final PdfExtractorClient pdfExtractorClient;
    private final CreditModelClient creditModelClient;
    private final UserServiceClient userServiceClient;

    private final Map<String, CacheData> documentDataCache = new HashMap<>();

    @Scheduled(fixedRate = 1800000)
    public void cleanupCache() {
        long currentTime = System.currentTimeMillis();
        long timeoutMillis = 1800000;
        documentDataCache.entrySet().removeIf(entry ->
                currentTime - entry.getValue().getTimestamp() > timeoutMillis);
    }

    public MyDataResponse getMyDataByPhoneNumber(String phoneNumber) {
        String sql = """
            SELECT user_pn, int_rate, installment, issue_d_period, debt, cr_line_period, pub_rec, revol_bal,
                   revol_util, open_acc, total_acc, mort_acc, collections_12_mths_ex_med, mortgage_debt,
                   mortgage_repayment, repayment, mortgage_term
            FROM mydata.user_log
            WHERE user_pn = ?
        """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                        new MyDataResponse(
                                rs.getFloat("int_rate"),
                                rs.getFloat("installment"),
                                rs.getInt("issue_d_period"),
                                rs.getFloat("debt"),
                                rs.getInt("cr_line_period"),
                                rs.getFloat("pub_rec"),
                                rs.getFloat("revol_bal"),
                                rs.getFloat("revol_util"),
                                rs.getFloat("open_acc"),
                                rs.getFloat("total_acc"),
                                rs.getFloat("mort_acc"),
                                rs.getFloat("collections_12_mths_ex_med"),
                                rs.getInt("mortgage_debt"),
                                rs.getInt("mortgage_repayment"),
                                rs.getInt("repayment"),
                                rs.getInt("mortgage_term")
                        ),
                phoneNumber
        );
    }

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

    public CreditEvaluationResponse evaluateCredit(CreditEvaluationRequest request) {
        // 마이데이터
        MyDataResponse myData = getMyDataByPhoneNumber(request.getPhoneNumber());

        // pdf 캐싱해둔거
        CacheData cacheData = documentDataCache.get(request.getPhoneNumber());
        if (cacheData == null) {
            throw new RuntimeException("No cached document data found");
        }

        DocumentExtractResponse<IncomeProofData> incomeProofResponse = cacheData.getIncomeProofData();
        DocumentExtractResponse<EmploymentCertificateData> employmentResponse = cacheData.getEmploymentCertificateData();

        if (incomeProofResponse == null || employmentResponse == null) {
            throw new RuntimeException("Required documents (Income Proof and Employment Certificate) must be processed first");
        }

        // 신용평가 모델 요청 데이터 구성
        CreditModelRequest modelRequest = CreditModelRequest.builder()
                .int_rate(myData.getIntRate())
                .installment(myData.getInstallment())
                .issue_d_period(myData.getIssueDPeriod())
                .dti(calculateDti(myData, incomeProofResponse.getData().getIncome()))
                .cr_line_period(myData.getCrLinePeriod())
                .open_acc(myData.getOpenAcc())
                .pub_rec(myData.getPubRec())
                .revol_bal(myData.getRevolBal())
                .revol_util(myData.getRevolUtil())
                .total_acc(myData.getTotalAcc())
                .mort_acc(myData.getMortAcc())
                .collections_12_mths_ex_med(myData.getCollections12MthsExMed())
                .emplength(employmentResponse.getData().getEmp_length())
                .annual_inc(incomeProofResponse.getData().getIncome())
                .loan_purpose(LoanPurpose.fromString(request.getPurpose()).getCode())
                .loan_amnt(request.getAmount())
                .build();

        // 신용평가 모델 호출
        CreditEvaluationResponse response = creditModelClient.evaluateCredit(modelRequest);
        documentDataCache.remove(request.getPhoneNumber());

        int convertedCreditRating = response.getTarget() + 1;

        log.info("Credit evaluation completed. Original rating: {}, Converted rating: {}",
                response.getTarget(), convertedCreditRating);

        try {
            UserCreditUpdateRequest userRequest = new UserCreditUpdateRequest(
                    request.getPhoneNumber(),
                    convertedCreditRating
            );
            log.info("Sending credit update request to user service: {}", userRequest);
            updateUserCredit(userRequest);
            log.info("Successfully updated user credit rating");
        } catch (Exception e) {
            log.error("Failed to update user credit rating", e);
            // 여기서 예외를 다시 던질지 여부는 비즈니스 요구사항에 따라 결정
        }
        return response;
    }

    private float calculateDti(MyDataResponse myDataResponse, float income) {
        float mortgage_debt = myDataResponse.getMortgageDebt();
        float mortgage_repayment = myDataResponse.getMortgageRepayment();
        float installment = myDataResponse.getInstallment();
        float mortgage_term = myDataResponse.getMortgageTerm();

        return ((mortgage_debt / mortgage_term) + mortgage_repayment + installment) / income * 100;
    }

    private void updateUserCredit(UserCreditUpdateRequest request) {
        log.info("Attempting to update user credit: {}", request);
        try {
            userServiceClient.updateCredit(request);
            log.info("Successfully updated user credit");
        } catch (Exception e) {
            log.error("Failed to update user credit", e);
            throw e;
        }
    }
}