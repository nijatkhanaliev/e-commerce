package com.company.model.event;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class OrderItemEvent implements Serializable {
    private Long productId;
    private int quantity;

    public OrderItemEvent(Long productId, int quantity){
        this.productId = productId;
        this.quantity = quantity;
    }

}
