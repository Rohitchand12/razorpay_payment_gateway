package com.rohit.razorpay.payment.service;

import com.rohit.razorpay.payment.dto.request.OrderCreateRequestDto;
import com.rohit.razorpay.payment.dto.response.OrderCreateResponseDto;

import java.util.UUID;

public interface OrderService {
    OrderCreateResponseDto create(UUID merchantId, OrderCreateRequestDto request);
}
