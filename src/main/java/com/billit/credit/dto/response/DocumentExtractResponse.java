package com.billit.credit.dto.response;

import com.billit.credit.dto.DocumentData;
import com.billit.credit.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentExtractResponse {
    private DocumentType documentType;
    private DocumentData data;
}
