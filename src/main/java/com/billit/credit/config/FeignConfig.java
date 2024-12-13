package com.billit.credit.config;

import feign.Client;
import feign.RequestInterceptor;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FeignConfig {
    @Value("${feign.client.config.user-service.url}")
    private String userServiceUrl;

    @PostConstruct
    public void init() {
        log.info("Configured user-service URL: {}", userServiceUrl);
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("X-Service-Name", "credit-proxy-service");
            log.info("Making request to URL: {}", requestTemplate.url());
            log.info("With headers: {}", requestTemplate.headers());
        };
    }
    @Bean
    public Client feignClient() {
        return new Client.Default(null, null);
    }
}
