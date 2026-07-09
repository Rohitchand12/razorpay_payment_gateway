package com.rohit.razorpay.payment.service;

import com.rohit.razorpay.payment.dto.request.OrderCreateRequestDto;
import com.rohit.razorpay.payment.dto.response.OrderResponseDto;
import com.rohit.razorpay.payment.dto.response.PaymentResponseDto;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponseDto create(UUID merchantId, OrderCreateRequestDto request);
    OrderResponseDto getById(UUID merchantId, UUID id);
    OrderResponseDto cancel(UUID merchantId, UUID id);
    List<PaymentResponseDto> listPayments(UUID merchantId, UUID id);
}
