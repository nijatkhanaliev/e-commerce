package com.company.service;

import com.company.model.dto.request.OrderRequest;
import com.company.model.dto.response.OrderResponse;

public interface OrderService {

    OrderResponse createOrder(OrderRequest orderRequest, Long userId);

}
