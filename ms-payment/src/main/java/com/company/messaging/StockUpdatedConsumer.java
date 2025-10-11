package com.company.messaging;


import com.company.exception.AccountBlockedException;
import com.company.exception.InsufficientBalanceException;
import com.company.model.events.StockUpdatedEvent;
import com.company.service.impl.PaymentEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.company.config.RabbitMQConfig.STOCK_UPDATED_QUEUE;


@Slf4j
@Component
@RequiredArgsConstructor
public class StockUpdatedConsumer {

    private final PaymentEventPublisher paymentEventPublisher;

    private int count = 0;

    @RabbitListener(queues = STOCK_UPDATED_QUEUE)
    private void handleStockUpdated(StockUpdatedEvent event) {
        try {
            paymentEventPublisher.handlePayment(event);
        } catch (AccountBlockedException | InsufficientBalanceException ex) {
            log.warn("STOCK.UPDATED.FAILED, message {}", ex.getMessage());
            paymentEventPublisher.handlePaymentFailed(event.getOrderId(), ex.getMessage());
        } catch (Exception ex) {
            log.info("STOCK.UPDATED.FAILED, message {}", ex.getMessage());
            count++;
            if (count == 6) {
                log.error("STOCK.UPDATED.EVENT in payment, exception happened, retryCount {}." +
                        " Message '{}'", count, ex.getMessage());
                paymentEventPublisher.handlePaymentFailed(event.getOrderId(), ex.getMessage());
            }
            count = 0;
            throw ex;
        }
    }

}
