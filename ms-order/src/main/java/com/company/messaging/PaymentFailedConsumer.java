package com.company.messaging;

import com.company.dao.entity.Order;
import com.company.dao.repository.OrderRepository;
import com.company.exception.NotFoundException;
import com.company.model.enums.OrderStatus;
import com.company.model.events.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.company.config.RabbitMQConfig.PAYMENT_FAILED_QUEUE;
import static com.company.exception.constant.ErrorCode.DATA_NOT_FOUND;
import static com.company.exception.constant.ErrorMessage.DATA_NOT_FOUND_MESSAGE;
import static com.company.model.enums.OrderStatus.CANCELLED;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentFailedConsumer {

    private final OrderRepository orderRepository;

    @RabbitListener(queues = PAYMENT_FAILED_QUEUE)
    private void consumePaymentFailed(PaymentFailedEvent event) {
        log.info("ORDER.CREATED.PAYMENT.FAILED, orderId {}, reason '{}'",
                event.getOrderId(), event.getReason());
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(()-> new NotFoundException(DATA_NOT_FOUND_MESSAGE, DATA_NOT_FOUND));

        if(order.getStatus() == CANCELLED){
            log.warn("Order already cancelled, orderId {}", event.getOrderId());
            return;
        }

        order.setStatus(CANCELLED);
        orderRepository.save(order);
    }

}
