package com.billit.credit.cache;

import com.billit.credit.dto.EmploymentCertificateData;
import com.billit.credit.dto.IncomeProofData;
import com.billit.credit.dto.response.DocumentExtractResponse;
import com.billit.credit.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class CacheData {
    private final Map<DocumentType, Object> data;
    private long timestamp;

    @SuppressWarnings("unchecked")
    public DocumentExtractResponse<IncomeProofData> getIncomeProofData() {
        return (DocumentExtractResponse<IncomeProofData>) data.get(DocumentType.INCOME_PROOF);
    }

    @SuppressWarnings("unchecked")
    public DocumentExtractResponse<EmploymentCertificateData> getEmploymentCertificateData() {
        return (DocumentExtractResponse<EmploymentCertificateData>) data.get(DocumentType.EMPLOYMENT_CERTIFICATE);
    }
}
