package com.company.client;

import com.company.model.dto.AccountResponseDTO;
import com.company.model.dto.request.DecreaseAccountRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ACCOUNT-SERVICE", url = "http://localhost:8084/api/")
public interface AccountClient {

    @GetMapping("v1/accounts/{userId}")
    AccountResponseDTO getAccountByUserId(@PathVariable Long userId);

    @PostMapping("v1/accounts/{userId}/credit")
    void decreaseAccount(@PathVariable Long userId, @RequestBody DecreaseAccountRequest request);

}
