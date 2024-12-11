package com.billit.credit.service;

import com.billit.credit.dto.request.PdfProcessRequest;
import com.billit.credit.dto.response.PdfProcessResponse;
import com.billit.credit.entity.PdfData;
import com.billit.credit.enums.DocumentType;
import com.billit.credit.exception.BusinessException;
import com.billit.credit.exception.ErrorCode;
import com.billit.credit.repository.PdfDataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class PdfProcessingService {
    private final WebClient webClient;
    private final PdfDataRepository pdfDataRepository;

    public PdfProcessingService(
            @Qualifier("pdfServiceWebClient") WebClient webClient,
            PdfDataRepository pdfDataRepository
    ) {
        this.webClient = webClient;
        this.pdfDataRepository = pdfDataRepository;
    }

    @Value("${python.pdf.service.url}")
    private String pdfServiceUrl;

    public PdfProcessResponse processPdf(PdfProcessRequest request) {
        try {
            // Python 서비스 호출
            PdfProcessResponse response = webClient.post()
                    .uri(pdfServiceUrl + "/process")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(PdfProcessResponse.class)
                    .block();

            // 결과 저장
            if (response != null && response.isSuccess()) {
                PdfData pdfData = PdfData.builder()
                        .userBorrowId(request.getUserBorrowId())
                        .documentType(DocumentType.valueOf(request.getDocumentType()))
                        .fileUrl(request.getPdfUrl())
                        .extractedData(new ObjectMapper().writeValueAsString(response.getExtractedData()))
                        .build();

                pdfDataRepository.save(pdfData);
            }

            return response;

        } catch (Exception e) {
            log.error("PDF 처리 중 오류 발생: ", e);
            throw new BusinessException(ErrorCode.PDF_PROCESSING_ERROR);
        }
    }
}