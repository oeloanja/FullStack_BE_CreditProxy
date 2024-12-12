package com.billit.credit.client;

import com.billit.credit.dto.EmploymentCertificateData;
import com.billit.credit.dto.IncomeProofData;
import com.billit.credit.dto.response.DocumentExtractResponse;
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

    @Value("${python.pdf-service.url}")
    private String pdfExtractorUrl;

    public DocumentExtractResponse<IncomeProofData> extractIncomeProofData(String fileUrl) {
        log.info("Extracting income proof data from PDF. URL: {}", fileUrl);

        try {
            Map<String, Object> request = new HashMap<>();
            request.put("url", fileUrl);

            // Python 서버에서 직접 income 필드를 포함한 응답을 받음
            IncomeProofData response = restTemplate.postForObject(
                    pdfExtractorUrl + "/api/v1/screening/income",
                    request,
                    IncomeProofData.class
            );

            log.info("Received income proof response: {}", response);

            if (response == null) {
                log.error("Failed to extract income proof data: null response");
                throw new RuntimeException("Failed to extract income proof data");
            }

            return new DocumentExtractResponse<>(DocumentType.INCOME_PROOF, response);

        } catch (Exception e) {
            log.error("Error while extracting income proof data", e);
            throw new RuntimeException("Failed to extract income proof data", e);
        }
    }

    public DocumentExtractResponse<EmploymentCertificateData> extractEmploymentCertificateData(String fileUrl) {
        log.info("Extracting employment certificate data from PDF. URL: {}", fileUrl);

        try {
            Map<String, Object> request = new HashMap<>();
            request.put("url", fileUrl);

            EmploymentCertificateData response = restTemplate.postForObject(
                    pdfExtractorUrl + "/api/v1/screening/length",
                    request,
                    EmploymentCertificateData.class
            );

            log.info("Received employment certificate response: {}", response);

            if (response == null) {
                log.error("Failed to extract employment certificate data: null response");
                throw new RuntimeException("Failed to extract employment certificate data");
            }

            return new DocumentExtractResponse<>(DocumentType.EMPLOYMENT_CERTIFICATE, response);

        } catch (Exception e) {
            log.error("Error while extracting employment certificate data", e);
            throw new RuntimeException("Failed to extract employment certificate data", e);
        }
    }
}
