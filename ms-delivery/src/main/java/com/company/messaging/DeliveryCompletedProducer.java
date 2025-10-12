package com.company.messaging;

import com.company.model.event.DeliveryCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static com.company.config.RabbitMQConfig.DELIVERY_COMPLETED_ROUTING_KEY;
import static com.company.config.RabbitMQConfig.ORDER_EXCHANGE;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryCompletedProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(DeliveryCompletedEvent event){
        log.info("publishing delivery completed event. orderId {}", event.getOrderId());
        rabbitTemplate.convertAndSend(ORDER_EXCHANGE, DELIVERY_COMPLETED_ROUTING_KEY, event);
    }

}
