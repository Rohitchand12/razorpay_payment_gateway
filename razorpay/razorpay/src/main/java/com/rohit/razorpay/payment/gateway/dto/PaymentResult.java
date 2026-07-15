package com.rohit.razorpay.payment.gateway.dto;

public sealed interface PaymentResult permits PaymentResult.pending, PaymentResult.failure {
    record pending(String registrationReference) implements PaymentResult{}
    record failure(String errCode, String errorDescription) implements PaymentResult{}
}
