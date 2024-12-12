package com.billit.credit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class CreditApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreditApplication.class, args);
	}
	@Bean
	public WebClient pdfServiceWebClient(@Value("${python.pdf.service.url}") String pdfServiceUrl) {
		return WebClient.builder()
				.baseUrl(pdfServiceUrl)
				.build();
	}

//	@Bean
//	public WebClient creditServiceWebClient(@Value("${python.credit.service.url}") String creditServiceUrl) {
//		return WebClient.builder()
//				.baseUrl(creditServiceUrl)
//				.build();
//	}

}
