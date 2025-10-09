package com.company.service;

import com.company.model.dto.response.PaymentResponse;

public interface PaymentService {

    PaymentResponse getPaymentDetails(Long id, Long userId);

}
