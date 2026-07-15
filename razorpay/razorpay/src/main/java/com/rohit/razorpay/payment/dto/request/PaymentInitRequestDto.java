package com.rohit.razorpay.payment.dto.request;

import com.rohit.razorpay.common.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record PaymentInitRequestDto(
        @NotNull(message = "Order id is required")
        UUID orderId,
        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,

        Map<String, Object> methodDetails
) {
}
