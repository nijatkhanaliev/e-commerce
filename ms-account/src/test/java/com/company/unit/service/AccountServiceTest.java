package com.company.unit.service;

import com.company.dao.entity.Account;
import com.company.dao.repository.AccountRepository;
import com.company.exception.NotFoundException;
import com.company.model.dto.request.CreditRequest;
import com.company.model.dto.response.AccountResponse;
import com.company.model.mapper.AccountMapper;
import com.company.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.company.exception.constant.ErrorCode.DATA_NOT_FOUND;
import static com.company.exception.constant.ErrorMessage.DATA_NOT_FOUND_MESSAGE;
import static com.company.model.enums.AccountStatus.ACTIVE;
import static com.company.unit.constant.AccountTestConstant.ACCOUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Spy
    private AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void test_getAccountByUserId_returnSuccess() {
        when(accountRepository.findByUserId(anyLong())).thenReturn(Optional.of(ACCOUNT));

        AccountResponse accountResponse = accountService.getAccountByUserId(1L);

        assertEquals(accountResponse.getUserId(), ACCOUNT.getUserId());
        assertEquals(accountResponse.getId(), ACCOUNT.getId());
        assertEquals(accountResponse.getStatus(), ACCOUNT.getStatus());
        assertEquals(accountResponse.getBalance(), ACCOUNT.getBalance());

        verify(accountRepository, times(1)).findByUserId(anyLong());
        verify(accountMapper, times(1)).toAccountResponse(ACCOUNT);
    }

    @Test
    void test_getAccountByUserId_throwNotFoundException() {
        when(accountRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

        NotFoundException ex = Assertions.assertThrows(NotFoundException.class, () -> {
            accountService.getAccountByUserId(1L);
        });

        assertEquals(ex.getErrorMessage(), DATA_NOT_FOUND_MESSAGE);
        assertEquals(ex.getErrorCode(), DATA_NOT_FOUND);

        verify(accountRepository, times(1)).findByUserId(anyLong());
        verifyNoInteractions(accountMapper);
    }

    @Test
    void test_credit_returnSuccess(){
        Long userId = 1L;
        CreditRequest creditRequest = new CreditRequest();
        creditRequest.setAmount(BigDecimal.valueOf(200));

        Account account = new Account(1L, 1L, BigDecimal.valueOf(200), ACTIVE,
                LocalDateTime.now(), LocalDateTime.now());

        when(accountRepository.findByUserId(anyLong())).thenReturn(Optional.of(account));

        accountService.credit(userId, creditRequest);

        assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(400));
        verify(accountRepository, times(1)).save(account);
    }


    @Test
    void credit_shouldThrowException_whenAccountNotFound() {
        Long userId = 1L;
        CreditRequest creditRequest = new CreditRequest();
        creditRequest.setAmount(BigDecimal.valueOf(200));

        when(accountRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.credit(userId, creditRequest))
                .isInstanceOf(NotFoundException.class);

        verify(accountRepository, never()).save(any());
    }

}
