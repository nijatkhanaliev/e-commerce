package com.company.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class StockUpdatedEvent {
    private String eventId = UUID.randomUUID().toString();
    private Long orderId;
    private String status;
    private String reason;
}
