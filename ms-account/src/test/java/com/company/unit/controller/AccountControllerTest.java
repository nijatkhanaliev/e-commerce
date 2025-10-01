package com.company.unit.controller;

import com.company.controller.AccountController;
import com.company.model.dto.request.CreditRequest;
import com.company.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static com.company.unit.constant.AccountTestConstant.ACCOUNT_RESPONSE;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void test_getAccountByUserId_returnSuccess() throws Exception {
        when(accountService.getAccountByUserId(anyLong())).thenReturn(ACCOUNT_RESPONSE);

        mockMvc.perform(get("/v1/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.balance", is(200)))
                .andExpect(jsonPath("$.status", is("ACTIVE")));
    }

    @Test
    void test_credit_returnSuccess() throws Exception {
        CreditRequest request = new CreditRequest();
        request.setAmount(BigDecimal.valueOf(100));

        doNothing().when(accountService).credit(anyLong(), any(CreditRequest.class));


        mockMvc.perform(post("/v1/accounts/1/credit")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());
    }

}
