package com.billit.credit.client;

import com.billit.credit.dto.response.DocumentExtractResponse;
import com.billit.credit.enums.DocumentType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PdfExtractorClient {
    private final RestTemplate restTemplate;

    @Value("${services.pdf-extractor.url}")
    private String pdfExtractorUrl;

    public DocumentExtractResponse extractData(String fileUrl, DocumentType documentType) {
        String endpoint = switch (documentType) {
            case INCOME_PROOF -> "/api/v1/screening/income";
            case EMPLOYMENT_CERTIFICATE -> "/api/v1/screening/length";
        };

        Map<String, Object> request = new HashMap<>();
        request.put("fileUrl", fileUrl);

        return restTemplate.postForObject(pdfExtractorUrl + endpoint, request, DocumentExtractResponse.class);
    }
}
