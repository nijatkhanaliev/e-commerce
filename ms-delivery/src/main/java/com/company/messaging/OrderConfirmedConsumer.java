package com.company.messaging;

import com.company.model.event.OrderConfirmedEvent;
import com.company.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.company.config.RabbitMQConfig.ORDER_CONFIRMED_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderConfirmedConsumer {

    private final DeliveryService deliveryService;

    @RabbitListener(queues = ORDER_CONFIRMED_QUEUE)
    private void consume(OrderConfirmedEvent event) {
        deliveryService.scheduleDelivery(event);
    }

}
