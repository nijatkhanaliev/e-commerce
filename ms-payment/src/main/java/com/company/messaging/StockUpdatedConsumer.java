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

    @RabbitListener(queues = STOCK_UPDATED_QUEUE)
    private void handleStockUpdated(StockUpdatedEvent event) {
        try {
            paymentEventPublisher.handlePayment(event);
        } catch (AccountBlockedException | InsufficientBalanceException ex) {
            log.warn("STOCK.UPDATED.FAILED, message {}", ex.getMessage());
            paymentEventPublisher.handlePaymentFailed(event, ex.getMessage());
        } catch (Exception ex) {
            log.error("STOCK.UPDATED.EVENT in payment, exception happened." +
                    " Message '{}'", ex.getMessage());
            paymentEventPublisher.handlePaymentFailed(event, ex.getMessage());
        }
    }

}
