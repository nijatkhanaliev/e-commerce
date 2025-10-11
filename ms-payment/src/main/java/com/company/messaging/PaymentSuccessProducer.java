package com.company.messaging;

import com.company.model.events.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentSuccessProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(String exchange, String routingKey, PaymentSuccessEvent event){
        log.info("Sending payment success event, orderId {}", event.getOrderId());
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }

}
