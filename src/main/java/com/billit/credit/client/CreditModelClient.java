package com.billit.credit.client;

import com.billit.credit.dto.request.CreditModelRequest;
import com.billit.credit.dto.response.CreditEvaluationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class CreditModelClient {
    private final RestTemplate restTemplate;

    @Value("${python.credit-model.url}")
    private String creditModelUrl;

    public CreditEvaluationResponse evaluateCredit(CreditModelRequest request) {
        // 모델 api 나오면 엔드포인트 맞춰야함
        return restTemplate.postForObject(creditModelUrl + "/predict", request, CreditEvaluationResponse.class);
    }
}
