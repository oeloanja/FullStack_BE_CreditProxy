package com.billit.credit.client;

import com.billit.credit.dto.DocumentData;
import com.billit.credit.dto.response.DocumentExtractResponse;
import com.billit.credit.dto.response.RawExtractResponse;
import com.billit.credit.enums.DocumentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PdfExtractorClient {
    private final RestTemplate restTemplate;

    @Value("${services.pdf-extractor.url}")
    private String pdfExtractorUrl;

    public DocumentExtractResponse extractData(String fileUrl, DocumentType documentType) {
        log.info("Extracting data from PDF. URL: {}, Type: {}", fileUrl, documentType);

        try {
            String endpoint = switch (documentType) {
                case INCOME_PROOF -> "/api/v1/screening/income";
                case EMPLOYMENT_CERTIFICATE -> "/api/v1/screening/length";
            };

            Map<String, Object> request = new HashMap<>();
            request.put("url", fileUrl);

            // Python 서버로부터 raw 응답 받기
            RawExtractResponse rawResponse = restTemplate.postForObject(
                    pdfExtractorUrl + endpoint,
                    request,
                    RawExtractResponse.class
            );

            log.info("Received raw response: {}", rawResponse);

            if (rawResponse == null) {
                log.error("Failed to extract data from PDF: null response");
                throw new RuntimeException("Failed to extract data from PDF");
            }

            // DocumentData로 변환
            DocumentData documentData = switch (documentType) {
                case INCOME_PROOF -> new DocumentData(null, rawResponse.getIncome());
                case EMPLOYMENT_CERTIFICATE -> new DocumentData(rawResponse.getEnp_length(), null);
            };

            return new DocumentExtractResponse(documentType, documentData);

        } catch (Exception e) {
            log.error("Error while extracting data from PDF", e);
            throw new RuntimeException("Failed to extract data from PDF", e);
        }
    }
}
