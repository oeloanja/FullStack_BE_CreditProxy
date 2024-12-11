package com.billit.credit.service;

import com.billit.credit.dto.request.CreditEvalRequest;
import com.billit.credit.dto.request.CreditModelRequest;
import com.billit.credit.dto.response.CreditEvalResponse;
import com.billit.credit.entity.MyData;
import com.billit.credit.entity.PdfData;
import com.billit.credit.enums.DocumentType;
import com.billit.credit.exception.BusinessException;
import com.billit.credit.exception.ErrorCode;
import com.billit.credit.repository.MyDataRepository;
import com.billit.credit.repository.PdfDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class CreditEvaluationService {
    private final WebClient webClient;
    private final MyDataRepository myDataRepository;
    private final PdfDataRepository pdfDataRepository;

    @Value("${python.credit.service.url}")
    private String creditServiceUrl;

    public CreditEvaluationService(
            @Qualifier("creditServiceWebClient") WebClient webClient,
            MyDataRepository myDataRepository,
            PdfDataRepository pdfDataRepository
    ) {
        this.webClient = webClient;
        this.myDataRepository = myDataRepository;
        this.pdfDataRepository = pdfDataRepository;
    }

    public CreditEvalResponse evaluateCredit(CreditEvalRequest request) {
        try {
            // 1. 마이데이터 조회
            MyData myData = myDataRepository.findByUserId(request.getUserBorrowId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.MYDATA_NOT_FOUND));

            // 2. PDF 데이터 조회
            Map<DocumentType, PdfData> pdfDataMap = new HashMap<>();
            for (DocumentType type : DocumentType.values()) {
                PdfData pdfData = pdfDataRepository.findLatestByUserIdAndType(request.getUserBorrowId(), type)
                        .orElseThrow(() -> new BusinessException(ErrorCode.PDF_NOT_FOUND));
                pdfDataMap.put(type, pdfData);
            }

            // 3. 신용평가 모델 호출
            CreditModelRequest modelRequest = CreditModelRequest.builder()
                    .myData(myData)
                    .pdfData(pdfDataMap)
                    .loanAmount(request.getLoanAmount())
                    .term(request.getTerm())
                    .purpose(request.getPurpose())
                    .build();

            return webClient.post()
                    .uri(creditServiceUrl + "/evaluate")
                    .bodyValue(modelRequest)
                    .retrieve()
                    .bodyToMono(CreditEvalResponse.class)
                    .block();

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("신용평가 중 오류 발생: ", e);
            throw new BusinessException(ErrorCode.CREDIT_EVAL_ERROR);
        }
    }
}

