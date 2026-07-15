package com.rohit.razorpay.payment.gateway.dto;

import com.rohit.razorpay.common.entity.Money;
import com.rohit.razorpay.common.enums.PaymentMethod;

import java.util.Map;
import java.util.UUID;

public record PaymentRequest(
    UUID paymentId,
    UUID merchantId,
    UUID orderId,
    Money amount,
    PaymentMethod paymentMethod,
    Map<String,Object> methodDetails
) {
}
