package com.company.model.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DeliveryCompletedEvent {
    private Long orderId;
    private Long userId;
}
