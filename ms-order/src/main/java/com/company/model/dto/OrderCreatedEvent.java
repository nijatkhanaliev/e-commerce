package com.company.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OrderCreatedEvent implements Serializable {
    private String eventId = UUID.randomUUID().toString();
    private Long orderId;
    private BigDecimal totalPrice;
    private List<OrderItemEvent> orderItemEvents;
}
