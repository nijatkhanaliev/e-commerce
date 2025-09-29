package com.company.messaging;

import com.company.exception.InsufficientStockException;
import com.company.model.dto.OrderCreatedEvent;
import com.company.model.dto.OrderCreatedEventResponse;
import com.company.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.company.config.RabbitMQConfig.ORDER_CREATED_QUEUE;
import static com.company.config.RabbitMQConfig.STOCK_EXCHANGE;
import static com.company.config.RabbitMQConfig.STOCK_UPDATED_ROUTING_KEY;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer {

    private final ProductService productService;
    private final StockUpdatedProducer stockUpdatedProducer;

    @RabbitListener(queues = ORDER_CREATED_QUEUE)
    private void handleAccountCreated(OrderCreatedEvent event) {
        try {
            log.info("Processing account created event: {}", event);
            productService.updateStock(event.getProductId(), event.getQuantity());
            OrderCreatedEventResponse orderCreatedEventResponse = new OrderCreatedEventResponse();
            orderCreatedEventResponse.setStatus("SUCCESSFUL");
            stockUpdatedProducer.send(STOCK_EXCHANGE, STOCK_UPDATED_ROUTING_KEY, orderCreatedEventResponse);

        } catch (InsufficientStockException ex) {
            log.error("Failed to process order.created event. message: {}", ex.getErrorMessage());
            OrderCreatedEventResponse orderCreatedEventResponse = new OrderCreatedEventResponse();
            orderCreatedEventResponse.setStatus("FAILED");
            stockUpdatedProducer.send(STOCK_EXCHANGE, STOCK_UPDATED_ROUTING_KEY, orderCreatedEventResponse);

            throw ex;
        }
    }

}
