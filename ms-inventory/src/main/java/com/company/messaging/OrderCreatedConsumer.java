package com.company.messaging;

import com.company.exception.InsufficientStockException;
import com.company.model.dto.OrderCreatedEvent;
import com.company.model.dto.StockUpdatedEvent;
import com.company.model.dto.OrderItemEvent;
import com.company.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.company.config.RabbitMQConfig.ORDER_CREATED_QUEUE;
import static com.company.config.RabbitMQConfig.ORDER_EXCHANGE;
import static com.company.config.RabbitMQConfig.STOCK_UPDATED_ROUTING_KEY;
import static com.company.exception.constant.ErrorCode.IN_SUFFICIENT_STOCK;
import static com.company.exception.constant.ErrorMessage.IN_SUFFICIENT_STOCK_MESSAGE;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer {

    private final ProductService productService;
    private final StockUpdatedProducer stockUpdatedProducer;

    @RabbitListener(queues = ORDER_CREATED_QUEUE)
    private void handleOrderCreated(OrderCreatedEvent event) {
        try {
            log.info("Processing account created, eventId: {}", event.getEventId());
            List<OrderItemEvent> orderItemEventList = event.getOrderItemEvents();

            if (orderItemEventList.isEmpty()) {
                log.error("Order.Item.Event is empty, orderId {}", event.getOrderId());
                throw new InsufficientStockException(IN_SUFFICIENT_STOCK_MESSAGE, IN_SUFFICIENT_STOCK);
            }
            orderItemEventList.forEach((item) -> {
                productService.updateStock(item.getProductId(), (-item.getQuantity()));
            });

            StockUpdatedEvent stockUpdatedEvent = new StockUpdatedEvent();
            stockUpdatedEvent.setOrderId(event.getOrderId());
            stockUpdatedEvent.setStatus("SUCCESS");
            stockUpdatedProducer.send(ORDER_EXCHANGE, STOCK_UPDATED_ROUTING_KEY, stockUpdatedEvent);

        } catch (InsufficientStockException ex) {
            log.error("Failed to process order.created event. message: {}", ex.getErrorMessage());
            StockUpdatedEvent stockUpdatedEvent = new StockUpdatedEvent();
            stockUpdatedEvent.setOrderId(event.getOrderId());
            stockUpdatedEvent.setStatus("FAILED");
            stockUpdatedEvent.setReason(ex.getErrorMessage());
            stockUpdatedProducer.send(ORDER_EXCHANGE, STOCK_UPDATED_ROUTING_KEY, stockUpdatedEvent);
        }
    }

}
