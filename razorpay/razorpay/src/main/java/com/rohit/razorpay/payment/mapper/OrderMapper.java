package com.rohit.razorpay.payment.mapper;

import com.rohit.razorpay.payment.dto.response.OrderResponseDto;
import com.rohit.razorpay.payment.entity.OrderRecordEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {
    OrderResponseDto toOrderResponseDto(OrderRecordEntity order);
}
