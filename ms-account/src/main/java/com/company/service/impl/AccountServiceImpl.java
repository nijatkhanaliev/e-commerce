package com.company.service.impl;

import com.company.dao.entity.Account;
import com.company.dao.repository.AccountRepository;
import com.company.exception.AlreadyExistsException;
import com.company.exception.NotFoundException;
import com.company.model.dto.request.BalanceUpdationRequest;
import com.company.model.dto.request.CreditRequest;
import com.company.model.dto.response.AccountResponse;
import com.company.model.mapper.AccountMapper;
import com.company.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.company.exception.constant.ErrorCode.ALREADY_EXISTS;
import static com.company.exception.constant.ErrorCode.DATA_NOT_FOUND;
import static com.company.exception.constant.ErrorMessage.ALREADY_EXISTS_MESSAGE;
import static com.company.exception.constant.ErrorMessage.DATA_NOT_FOUND_MESSAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public AccountResponse getAccountByUserId(Long userId) {
        log.info("Getting user account by userId, userId {}", userId);
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(DATA_NOT_FOUND_MESSAGE, DATA_NOT_FOUND));

        return accountMapper.toAccountResponse(account);
    }

    @Override
    public void credit(Long userId, CreditRequest request) {
        log.info("Credit called, userId {}", userId);
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(DATA_NOT_FOUND_MESSAGE, DATA_NOT_FOUND));

        BigDecimal currentBalance = account.getBalance();
        BigDecimal updatedBalance = currentBalance.add(request.getAmount());
        account.setBalance(updatedBalance);
        accountRepository.save(account);
    }

    @Override
    public void createAccount(Long userId) {
        log.info("Creating account, userId {}", userId);
        if (accountRepository.existsByUserId(userId)) {
            throw new AlreadyExistsException(ALREADY_EXISTS_MESSAGE, ALREADY_EXISTS);
        }

        Account account = new Account();
        account.setUserId(userId);
        account.setBalance(BigDecimal.ZERO);
        accountRepository.save(account);
    }

    @Override
    public void updateUserBalance(Long userId, BalanceUpdationRequest request) {
        log.info("Updating user balance, userId {}, newBalance {}", userId, request.getBalance());
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(DATA_NOT_FOUND_MESSAGE, DATA_NOT_FOUND));

        account.setBalance(request.getBalance());
        accountRepository.save(account);
    }

}
