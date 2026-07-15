package com.rohit.razorpay.processor.dto;

import com.rohit.razorpay.common.entity.Money;
import com.rohit.razorpay.common.enums.PaymentMethod;

import java.util.Map;

public record PaymentProcessorRequest(
        PaymentMethod method,
        Money amount,
        Map<String,Object> methodDetails
) {
}
