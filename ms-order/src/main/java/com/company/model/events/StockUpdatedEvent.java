package com.company.model.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockUpdatedEvent {
    private Long orderId;
    private String status;
    private String reason;
}
