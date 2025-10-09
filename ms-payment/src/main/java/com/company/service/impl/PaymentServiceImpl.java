package com.company.service.impl;

import com.company.dao.entity.Payment;
import com.company.dao.repository.PaymentRepository;
import com.company.exception.NotFoundException;
import com.company.model.dto.response.PaymentResponse;
import com.company.model.mapper.PaymentMapper;
import com.company.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.company.exception.constant.ErrorCode.DATA_NOT_FOUND;
import static com.company.exception.constant.ErrorMessage.DATA_NOT_FOUND_MESSAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public PaymentResponse getPaymentDetails(Long id, Long userId) {
        log.info("Getting payment details, paymentId {}, userId {}", id, userId);
        Payment payment = paymentRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException(DATA_NOT_FOUND_MESSAGE, DATA_NOT_FOUND));

        return paymentMapper.toPaymentResponse(payment);
    }

}
