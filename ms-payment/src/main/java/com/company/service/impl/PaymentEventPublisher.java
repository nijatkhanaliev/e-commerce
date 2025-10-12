package com.company.service.impl;

import com.company.client.AccountClient;
import com.company.dao.entity.Payment;
import com.company.dao.repository.PaymentRepository;
import com.company.exception.AccountBlockedException;
import com.company.exception.InsufficientBalanceException;
import com.company.messaging.PaymentFailedProducer;
import com.company.messaging.PaymentSuccessProducer;
import com.company.model.dto.AccountResponseDTO;
import com.company.model.dto.request.DecreaseAccountRequest;
import com.company.model.events.PaymentFailedEvent;
import com.company.model.events.PaymentSuccessEvent;
import com.company.model.events.StockUpdatedEvent;
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
import static com.company.model.enums.PaymentStatus.SUCCESS;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final PaymentRepository paymentRepository;
    private final AccountClient accountClient;
    private final PaymentSuccessProducer paymentSuccessProducer;
    private final PaymentFailedProducer paymentFailedProducer;

    @Transactional
    public void handlePayment(StockUpdatedEvent event) {

        Long userId = event.getUserId();
        BigDecimal totalPrice = event.getTotalPrice();
        log.info("Handle Order Created Payment. Getting user account. userId {}", userId);
        AccountResponseDTO accountResponse = accountClient.getAccountByUserId(userId);
        BigDecimal userBalance = accountResponse.getBalance();
        if ("BLOCKED".equals(accountResponse.getStatus())) {
            throw new AccountBlockedException(ACCOUNT_BLOCKED_MESSAGE, ACCOUNT_BLOCKED);
        }

        if (totalPrice.compareTo(userBalance) > 0) {
            throw new InsufficientBalanceException(INSUFFICIENT_BALANCE_MESSAGE, INSUFFICIENT_BALANCE);
        }

        DecreaseAccountRequest decreaseAccountRequest = DecreaseAccountRequest.builder()
                .balance(userBalance.subtract(totalPrice))
                .build();

        accountClient.updateUserBalance(userId, decreaseAccountRequest);

        Payment payment = new Payment();
        payment.setOrderId(event.getOrderId());
        payment.setStatus(SUCCESS);
        payment.setUserId(event.getUserId());
        payment.setAmount(totalPrice);
        paymentRepository.save(payment);

        PaymentSuccessEvent successfulPaymentEvent = PaymentSuccessEvent
                .builder()
                .orderId(event.getOrderId())
                .status("SUCCESS")
                .build();

        paymentSuccessProducer.send(ORDER_CREATED_EXCHANGE, PAYMENT_SUCCESS_ROUTING_KEY, successfulPaymentEvent);
    }


    public void handlePaymentFailed(StockUpdatedEvent event, String message) {
        log.info("handleOrderCreatedPaymentFailed method called. orderId {}", event.getOrderId());
        PaymentFailedEvent failedPaymentEvent = PaymentFailedEvent.builder()
                .orderId(event.getOrderId())
                .userId(event.getUserId())
                .reason(message)
                .orderItemDtos(event.getOrderItemDtos())
                .build();

        paymentFailedProducer.send(ORDER_CREATED_EXCHANGE, PAYMENT_FAILED_ROUTING_KEY, failedPaymentEvent);
    }

}
