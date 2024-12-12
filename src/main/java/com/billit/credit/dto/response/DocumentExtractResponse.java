package com.billit.credit.dto.response;

import com.billit.credit.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentExtractResponse<T> {
    private DocumentType documentType;
    private T data;
}
