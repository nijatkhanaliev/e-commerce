package com.company.model.mapper;

import com.company.dao.entity.Payment;
import com.company.model.dto.response.PaymentResponse;
import org.mapstruct.Mapper;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface PaymentMapper {

    PaymentResponse toPaymentResponse(Payment payment);

}
