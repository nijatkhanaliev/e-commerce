package com.company.service;

import com.company.dao.entity.Delivery;
import com.company.dao.repository.DeliveryRepository;
import com.company.messaging.DeliveryCompletedProducer;
import com.company.model.event.DeliveryCompletedEvent;
import com.company.model.event.OrderConfirmedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.company.model.enums.DeliveryStatus.COMPLETED;
import static com.company.model.enums.DeliveryStatus.FAILED;
import static com.company.model.enums.DeliveryStatus.SCHEDULED;
import static java.time.temporal.ChronoUnit.SECONDS;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryCompletedProducer deliveryCompletedProducer;

    public void scheduleDelivery(OrderConfirmedEvent event) {
        log.info("Scheduling delivery, orderId {}", event.getOrderId());
        Long orderId = event.getOrderId();
        Long userId = event.getUserId();

        Delivery delivery = new Delivery();
        delivery.setOrderId(orderId);
        delivery.setUserId(userId);
        delivery.setStatus(SCHEDULED);
        deliveryRepository.save(delivery);

        try {
            log.info("Simulating delivery completion. orderId {}", orderId);
            simulateDeliveryCompletion(delivery);
            DeliveryCompletedEvent deliveryCompletedEvent = DeliveryCompletedEvent.builder()
                    .orderId(orderId)
                    .userId(userId)
                    .build();
            deliveryCompletedProducer.send(deliveryCompletedEvent);
        } catch (InterruptedException e) {
            log.error("Delivery processing failed. orderId {}, message '{}'", orderId, e.getMessage());
            delivery.setStatus(FAILED);
            deliveryRepository.save(delivery);
        }
    }

    private void simulateDeliveryCompletion(Delivery delivery) throws InterruptedException {
        Thread.sleep(Duration.of(20, SECONDS));
        delivery.setStatus(COMPLETED);
        delivery.setDeliveredAt(LocalDateTime.now());
        deliveryRepository.save(delivery);
    }

}
