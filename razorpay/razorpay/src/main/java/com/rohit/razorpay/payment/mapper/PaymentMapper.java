package com.rohit.razorpay.payment.mapper;

import com.rohit.razorpay.payment.dto.response.PaymentResponseDto;
import com.rohit.razorpay.payment.entity.PaymentEntity;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {
    @Mapping(target = "orderId", source = "order.id")
    PaymentResponseDto toPaymentResponseDto(PaymentEntity paymentEntity);

    @InheritConfiguration
    List<PaymentResponseDto> toPaymentResponseDtoList(List<PaymentEntity> payments);

}
