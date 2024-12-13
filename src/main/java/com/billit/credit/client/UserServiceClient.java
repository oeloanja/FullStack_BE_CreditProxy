package com.billit.credit.client;


import com.billit.credit.dto.request.UserCreditUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "USER-SERVICE", url="${feign.client.config.user-service.url}")
public interface UserServiceClient {
    @PutMapping("/api/v1/user-service/users/borrow/credit")
    ResponseEntity<Void> updateCredit(@RequestBody UserCreditUpdateRequest request);
}
