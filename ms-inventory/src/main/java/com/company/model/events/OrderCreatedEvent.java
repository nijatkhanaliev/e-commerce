package com.company.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class OrderCreatedEvent {
    private String eventId;
    private Long orderId;
    private BigDecimal totalPrice;
    private List<OrderItemEvent> orderItemEvents;
}
