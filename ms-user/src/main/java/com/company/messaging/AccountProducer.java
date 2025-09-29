package com.company.messaging;

import com.company.model.dto.AccountCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountProducer {
    
    private final RabbitTemplate rabbitTemplate;

    public void send(String exchange, String routingKey, AccountCreatedEvent event) {
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }

}
