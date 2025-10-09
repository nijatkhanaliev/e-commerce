package com.company.service.impl;

import com.company.dao.entity.Product;
import com.company.dao.repository.ProductRepository;
import com.company.exception.InvalidOrderItemsException;
import com.company.exception.NotFoundException;
import com.company.messaging.StockUpdatedProducer;
import com.company.model.dto.OrderItemDto;
import com.company.model.events.OrderCreatedEvent;
import com.company.model.events.StockUpdatedEvent;
import com.company.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.company.config.RabbitMQConfig.ORDER_EXCHANGE;
import static com.company.config.RabbitMQConfig.STOCK_UPDATED_ROUTING_KEY;
import static com.company.exception.constant.ErrorCode.DATA_NOT_FOUND;
import static com.company.exception.constant.ErrorCode.INVALID_ORDER_ITEMS;
import static com.company.exception.constant.ErrorMessage.DATA_NOT_FOUND_MESSAGE;
import static com.company.exception.constant.ErrorMessage.INVALID_ORDER_ITEMS_MESSAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockEventPublisher {

    private final ProductRepository productRepository;
    private final ProductService productService;
    private final StockUpdatedProducer stockUpdatedProducer;

    public void handleStockUpdatedFailed(Long orderId, String exceptionMessage){
        log.error("Failed to process order.created event. message: {}", exceptionMessage);
        StockUpdatedEvent stockUpdatedEvent = new StockUpdatedEvent();
        stockUpdatedEvent.setOrderId(orderId);
        stockUpdatedEvent.setStatus("FAILED");
        stockUpdatedEvent.setReason(exceptionMessage);
        stockUpdatedProducer.send(ORDER_EXCHANGE, STOCK_UPDATED_ROUTING_KEY, stockUpdatedEvent);
    }

    @Transactional
    public void handleStockUpdated(OrderCreatedEvent event){
        log.info("Processing order created event, eventId: {}", event.getEventId());
        List<OrderItemDto> orderItemEventList = event.getOrderItemDtos();

        if (orderItemEventList.isEmpty()) {
            log.error("Order.Item.Event is empty, orderId {}", event.getOrderId());
            throw new InvalidOrderItemsException(INVALID_ORDER_ITEMS_MESSAGE,
                    INVALID_ORDER_ITEMS);
        }
        orderItemEventList.forEach((item) -> {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new NotFoundException(DATA_NOT_FOUND_MESSAGE,
                            DATA_NOT_FOUND));
            productService.updateStock(item.getProductId(),
                    (product.getStock() - item.getQuantity()));
        });

        StockUpdatedEvent stockUpdatedEvent = new StockUpdatedEvent();
        stockUpdatedEvent.setOrderId(event.getOrderId());
        stockUpdatedEvent.setStatus("RESERVED");
        stockUpdatedProducer.send(ORDER_EXCHANGE, STOCK_UPDATED_ROUTING_KEY, stockUpdatedEvent);
    }

}
