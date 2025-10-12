package com.company.service;

import com.company.model.dto.request.BalanceUpdationRequest;
import com.company.model.dto.request.CreditRequest;
import com.company.model.dto.response.AccountResponse;

public interface AccountService {

    AccountResponse getAccountByUserId(Long userId);

    void credit(Long userId, CreditRequest request);

    void createAccount(Long userId);

    void updateUserBalance(Long userId, BalanceUpdationRequest request);
}
