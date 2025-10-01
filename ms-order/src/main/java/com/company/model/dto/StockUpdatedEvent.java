package com.company.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockUpdatedEvent {
    private String eventId;
    private Long orderId;
    private String status;
    private String reason;
}
