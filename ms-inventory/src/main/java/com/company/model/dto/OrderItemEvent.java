package com.company.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemEvent {
    private Long productId;
    private int quantity;

    public OrderItemEvent(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
