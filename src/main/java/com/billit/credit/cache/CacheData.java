package com.billit.credit.cache;

import com.billit.credit.dto.response.DocumentExtractResponse;
import com.billit.credit.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class CacheData {
    private final Map<DocumentType, DocumentExtractResponse> data;
    private long timestamp;
}
