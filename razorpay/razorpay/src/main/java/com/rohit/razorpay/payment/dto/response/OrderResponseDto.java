package com.rohit.razorpay.payment.dto.response;

import com.rohit.razorpay.common.entity.Money;
import com.rohit.razorpay.common.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record OrderResponseDto(
        UUID id,
        UUID merchantId,
        Money amount,
        UUID customerId,
        OrderStatus status,
        Integer attempts,
        Map<String,Object> notes,
        LocalDateTime expiresAt
) {
}
