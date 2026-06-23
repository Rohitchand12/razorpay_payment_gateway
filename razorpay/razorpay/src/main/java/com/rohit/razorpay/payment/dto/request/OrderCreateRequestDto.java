package com.rohit.razorpay.payment.dto.request;

import com.rohit.razorpay.common.entity.Money;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Map;

public record OrderCreateRequestDto(

        @NotNull(message = "Amount is required")
        Money amount,

        @Size(max = 100)
        String receipt, //provided by merchant whatever format they have stored the order. ex order_123
        Map<String,Object> notes, //some metadata about order that merchant provides.
        LocalDateTime expiresAt
) {
}
