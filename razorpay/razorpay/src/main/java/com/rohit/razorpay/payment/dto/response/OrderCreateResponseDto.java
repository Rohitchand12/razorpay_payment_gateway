package com.rohit.razorpay.payment.dto.response;

import com.rohit.razorpay.common.entity.Money;
import com.rohit.razorpay.common.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record OrderCreateResponseDto(
        UUID id,
        UUID merchantId,
        Money amount,
        OrderStatus status,
        Integer attempts,
        Map<String,Object> notes,
        LocalDateTime expiresAt
) {
}
