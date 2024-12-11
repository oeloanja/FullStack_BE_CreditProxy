package com.billit.credit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// request, response는 모델에 맞게 조정해야함
public class PdfProcessResponse {
    private boolean success;
    private Map<String, String> extractedData;
}
