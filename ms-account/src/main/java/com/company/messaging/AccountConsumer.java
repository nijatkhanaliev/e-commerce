package com.company.messaging;

import com.company.model.dto.AccountCreatedEvent;
import com.company.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.company.config.RabbitMQConfig.ACCOUNT_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountConsumer {

    private final AccountService accountService;

    @RabbitListener(queues = ACCOUNT_QUEUE)
    private void handleAccountCreated(AccountCreatedEvent event){
        try {
            log.info("Processing account created event: {}", event);
            accountService.createAccount(event.getUserId());
        } catch (Exception ex) {
            log.error("Failed to process event, sending to DLQ", ex);
            throw ex;
        }
    }

}
