package com.company.integration;

import com.company.dao.entity.Account;
import com.company.dao.repository.AccountRepository;
import com.company.model.dto.response.AccountResponse;
import com.company.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.math.BigDecimal;

import static com.company.model.enums.AccountStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
public class AccountServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.4")
            .withDatabaseName("accountdb")
            .withUsername("postgres")
            .withPassword("admin");


    @Container
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3-management");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.rabbitmq.host", rabbit::getHost);
        registry.add("spring.rabbitmq.port", rabbit::getAmqpPort);
    }

    @Autowired
    private DataSource dataSource;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();

        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(200));
        account.setUserId(1L);
        account.setStatus(ACTIVE);

        accountRepository.save(account);
    }

    @Test
    void contextLoads() {
        assertThat(dataSource).isNotNull();
        assertThat(postgres.isRunning()).isTrue();
        assertThat(rabbit.isRunning()).isTrue();
    }

    @Test
    void test_getAccountByUserId() {
        AccountResponse accountResponse = accountService.getAccountByUserId(1L);

        assertEquals(accountResponse.getUserId(), 1);
    }

}
