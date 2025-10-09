package com.company.service.impl;

import com.company.client.AccountClient;
import com.company.dao.repository.PaymentRepository;
import com.company.exception.AccountBlockedException;
import com.company.exception.InsufficientBalanceException;
import com.company.messaging.PaymentFailedProducer;
import com.company.messaging.PaymentSuccessProducer;
import com.company.model.dto.AccountResponseDTO;
import com.company.model.dto.request.DecreaseAccountRequest;
import com.company.model.events.OrderCreatedEvent;
import com.company.model.events.PaymentFailedEvent;
import com.company.model.events.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.company.config.RabbitMQConfig.ORDER_CREATED_EXCHANGE;
import static com.company.config.RabbitMQConfig.PAYMENT_FAILED_ROUTING_KEY;
import static com.company.config.RabbitMQConfig.PAYMENT_SUCCESS_ROUTING_KEY;
import static com.company.exception.constant.ErrorCode.ACCOUNT_BLOCKED;
import static com.company.exception.constant.ErrorCode.INSUFFICIENT_BALANCE;
import static com.company.exception.constant.ErrorMessage.ACCOUNT_BLOCKED_MESSAGE;
import static com.company.exception.constant.ErrorMessage.INSUFFICIENT_BALANCE_MESSAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final PaymentRepository paymentRepository;
    private final AccountClient accountClient;
    private final PaymentSuccessProducer paymentSuccessProducer;
    private final PaymentFailedProducer paymentFailedProducer;


    @Transactional
    public void handleOrderCreatedPayment(OrderCreatedEvent event) {
        Long userId = event.getUserId();
        BigDecimal totalPrice = event.getTotalPrice();
        log.info("Handle Order Created Payment. Getting user account. userId {}", userId);
        AccountResponseDTO accountResponse = accountClient.getAccountByUserId(userId);

        if ("BLOCKED".equals(accountResponse.getStatus())) {
            throw new AccountBlockedException(ACCOUNT_BLOCKED_MESSAGE, ACCOUNT_BLOCKED);
        }

        if (totalPrice.compareTo(accountResponse.getBalance()) > 0) {
            throw new InsufficientBalanceException(INSUFFICIENT_BALANCE_MESSAGE, INSUFFICIENT_BALANCE);
        }
        DecreaseAccountRequest decreaseAccountRequest = DecreaseAccountRequest.builder()
                .totalPrice(totalPrice)
                .build();

        log.info("Decreasing user balance, userId {}", userId);
        accountClient.decreaseAccount(userId, decreaseAccountRequest);

        PaymentSuccessEvent successfulPaymentEvent = PaymentSuccessEvent
                .builder()
                .orderId(event.getOrderId())
                .status("SUCCESS")
                .build();

        paymentSuccessProducer.send(ORDER_CREATED_EXCHANGE, PAYMENT_SUCCESS_ROUTING_KEY, successfulPaymentEvent);
    }


    public void handleOrderCreatedPaymentFailed(Long orderId, String message) {
        log.info("handleOrderCreatedPaymentFailed method called. orderId {}", orderId);
        PaymentFailedEvent failedPaymentEvent = PaymentFailedEvent.builder()
                .orderId(orderId)
                .status("FAILED")
                .reason(message)
                .build();

        paymentFailedProducer.send(ORDER_CREATED_EXCHANGE, PAYMENT_FAILED_ROUTING_KEY, failedPaymentEvent);
    }

}
