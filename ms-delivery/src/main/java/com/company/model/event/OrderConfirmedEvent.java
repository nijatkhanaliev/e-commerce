package com.company.model.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderConfirmedEvent {
    private Long orderId;
    private Long userId;
}
