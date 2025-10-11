package com.company.model.events;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class StockUpdateEvent {
    private String eventId;
    private Long orderId;
    private Long userId;
    private BigDecimal totalPrice;
}
