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
import com.billit.credit.exception.CreditServiceException;
import com.billit.credit.exception.ErrorCode;
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
    // 외부 서비스 클라이언트와 데이터베이스 접근을 위한 의존성 주입
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
        log.debug("Cache cleanup completed. Current cache size: {}", documentDataCache.size());
    }

    public MyDataResponse getMyDataByPhoneNumber(String phoneNumber) {
        try {
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
        } catch (Exception e) {
            log.error("Failed to fetch MyData for phone number: {}", phoneNumber, e);
            throw new CreditServiceException(ErrorCode.MYDATA_NOT_FOUND,
                    "해당 전화번호로 마이데이터를 찾을 수 없습니다: " + phoneNumber);
        }
    }

    public DocumentExtractResponse<IncomeProofData> processIncomeProof(DocumentUrlRequest request) {
        try {
            DocumentExtractResponse<IncomeProofData> response = pdfExtractorClient.extractIncomeProofData(request.getFileUrl());
            updateDocumentCache(request.getPhoneNumber(), DocumentType.INCOME_PROOF, response);

            return response;
        } catch (Exception e) {
            throw new CreditServiceException(ErrorCode.DOCUMENT_PROCESSING_FAILED,
                    "소득증명원 처리 중 오류가 발생했습니다.");
        }
    }
    public DocumentExtractResponse<EmploymentCertificateData> processEmploymentCertificate(DocumentUrlRequest request) {
        try {
            DocumentExtractResponse<EmploymentCertificateData> response =
                    pdfExtractorClient.extractEmploymentCertificateData(request.getFileUrl());
            updateDocumentCache(request.getPhoneNumber(), DocumentType.EMPLOYMENT_CERTIFICATE, response);
            return response;
        } catch (Exception e) {
            throw new CreditServiceException(ErrorCode.DOCUMENT_PROCESSING_FAILED,
                    "재직증명서 처리 중 오류가 발생했습니다.");
        }
    }

    public CreditEvaluationResponse evaluateCredit(CreditEvaluationRequest request) {
        try {
            MyDataResponse myData = getMyDataByPhoneNumber(request.getPhoneNumber());

            CacheData cacheData = documentDataCache.get(request.getPhoneNumber());
            if (cacheData == null) {
                throw new CreditServiceException(ErrorCode.DOCUMENT_NOT_FOUND);
            }

            DocumentExtractResponse<IncomeProofData> incomeProofResponse = cacheData.getIncomeProofData();
            DocumentExtractResponse<EmploymentCertificateData> employmentResponse = cacheData.getEmploymentCertificateData();

            if (incomeProofResponse == null || employmentResponse == null) {
                throw new CreditServiceException(ErrorCode.REQUIRED_DOCUMENTS_MISSING);
            }
            CreditModelRequest modelRequest = buildCreditModelRequest(request, myData,
                    incomeProofResponse, employmentResponse);

            CreditEvaluationResponse response = creditModelClient.evaluateCredit(modelRequest);

            documentDataCache.remove(request.getPhoneNumber());

            int convertedCreditRating = response.getTarget() + 1;
            updateUserCreditRating(request.getPhoneNumber(), convertedCreditRating);

            return response;

        } catch (CreditServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new CreditServiceException(ErrorCode.CREDIT_EVALUATION_FAILED, e);
        }
    }

    private void updateUserCreditRating(String phoneNumber, int creditRating) {
        try {
            UserCreditUpdateRequest userRequest = new UserCreditUpdateRequest(
                    phoneNumber,
                    creditRating
            );
            userServiceClient.updateCredit(userRequest);
        } catch (Exception e) {
            throw new CreditServiceException(ErrorCode.USER_UPDATE_FAILED);
        }
    }

    private CreditModelRequest buildCreditModelRequest(
            CreditEvaluationRequest request,
            MyDataResponse myData,
            DocumentExtractResponse<IncomeProofData> incomeProofResponse,
            DocumentExtractResponse<EmploymentCertificateData> employmentResponse) {

        log.info("Building credit model request with input - request: {}, myData: {}, incomeProof: {}, employment: {}",
                request, myData, incomeProofResponse, employmentResponse);

        float annualIncome = incomeProofResponse.getData().getIncome();
        float dti = calculateDti(myData, annualIncome);

        try {
            CreditModelRequest creditModelRequest = CreditModelRequest.builder()
                    .int_rate(myData.getIntRate())
                    .installment(myData.getInstallment())
                    .issue_d_period(myData.getIssueDPeriod())
                    .dti(dti)
                    .cr_line_period(myData.getCrLinePeriod())
                    .open_acc(myData.getOpenAcc())
                    .pub_rec(myData.getPubRec())
                    .revol_bal(myData.getRevolBal())
                    .revol_util(myData.getRevolUtil())
                    .total_acc(myData.getTotalAcc())
                    .mort_acc(myData.getMortAcc())
                    .collections_12_mths_ex_med(myData.getCollections12MthsExMed())
                    .emplength(employmentResponse.getData().getEmp_length())
                    .annual_inc(annualIncome)
                    .loan_purpose(LoanPurpose.fromString(request.getPurpose()).getCode())
                    .loan_amnt(request.getAmount())
                    .build();

            log.info("Built credit model request: {}", creditModelRequest);
            return creditModelRequest;
        } catch (IllegalArgumentException e) {
            log.error("Failed to build credit model request due to invalid loan purpose: {}", request.getPurpose(), e);
            throw new CreditServiceException(ErrorCode.INVALID_LOAN_PURPOSE);
        }
    }

    private float calculateDti(MyDataResponse myDataResponse, float income) {
        float mortgage_debt = myDataResponse.getMortgageDebt();
        float mortgage_repayment = myDataResponse.getMortgageRepayment();
        float installment = myDataResponse.getInstallment();
        float mortgage_term = myDataResponse.getMortgageTerm();

        return ((mortgage_debt / mortgage_term) + mortgage_repayment + installment) / income * 100;
    }

    private void updateDocumentCache(String phoneNumber, DocumentType documentType,
                                     DocumentExtractResponse<?> response) {
        CacheData cacheData = documentDataCache.getOrDefault(
                phoneNumber,
                new CacheData(new HashMap<>(), System.currentTimeMillis())
        );
        cacheData.getData().put(documentType, response);
        cacheData.setTimestamp(System.currentTimeMillis());
        documentDataCache.put(phoneNumber, cacheData);
    }
}