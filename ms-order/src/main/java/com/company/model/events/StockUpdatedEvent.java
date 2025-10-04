package com.company.model.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockUpdatedEvent {
    private Long orderId;
    private String status;
    private String reason;
}
