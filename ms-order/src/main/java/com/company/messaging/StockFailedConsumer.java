package com.company.messaging;

import com.company.dao.entity.Order;
import com.company.dao.repository.OrderRepository;
import com.company.exception.NotFoundException;
import com.company.model.events.StockUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.company.config.RabbitMQConfig.STOCK_UPDATED_QUEUE;
import static com.company.exception.constant.ErrorCode.DATA_NOT_FOUND;
import static com.company.exception.constant.ErrorMessage.DATA_NOT_FOUND_MESSAGE;
import static com.company.model.enums.OrderStatus.CANCELLED;
import static com.company.model.enums.OrderStatus.CONFIRMED;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockFailedConsumer {

    private final OrderRepository orderRepository;

    @RabbitListener(queues = STOCK_UPDATED_QUEUE)
    private void consumerStockUpdated(StockUpdatedEvent event) {
        if ("FAILED".equals(event.getStatus())) {
            log.warn("Stock updated failed, orderId {}. Message '{}'",
                    event.getOrderId(), event.getReason());

            log.info("Order cancelling, orderId {}", event.getOrderId());
            Long orderId = event.getOrderId();
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new NotFoundException(DATA_NOT_FOUND_MESSAGE, DATA_NOT_FOUND));
            order.setStatus(CANCELLED);
            orderRepository.save(order);
        } else if ("RESERVED".equals(event.getStatus())) {
            log.info("Stock updated. orderId {}", event.getOrderId());
            Long orderId = event.getOrderId();
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new NotFoundException(DATA_NOT_FOUND_MESSAGE, DATA_NOT_FOUND));
            order.setStatus(CONFIRMED);
            orderRepository.save(order);
        }
    }

}
