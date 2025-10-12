package com.company.controller;

import com.company.model.dto.request.BalanceUpdationRequest;
import com.company.model.dto.request.CreditRequest;
import com.company.model.dto.response.AccountResponse;
import com.company.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{userId}")
    public ResponseEntity<AccountResponse> getAccountByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getAccountByUserId(userId));
    }

    @PostMapping("/{userId}/credit")
    public ResponseEntity<Void> credit(
            @PathVariable Long userId,
            @Valid @RequestBody CreditRequest request) {

        accountService.credit(userId, request);
        return ResponseEntity.ok()
                .build();
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Void> updateUserBalance(
            @PathVariable Long userId,
            @Valid @RequestBody BalanceUpdationRequest request) {

        accountService.updateUserBalance(userId, request);
        return ResponseEntity.ok()
                .build();
    }
}
