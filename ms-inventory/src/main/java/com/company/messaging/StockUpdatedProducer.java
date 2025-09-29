package com.company.messaging;

import com.company.model.dto.OrderCreatedEventResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockUpdatedProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(String exchange, String routingKey, OrderCreatedEventResponse event) {
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }

}
