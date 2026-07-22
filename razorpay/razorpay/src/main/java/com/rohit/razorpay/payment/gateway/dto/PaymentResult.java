package com.rohit.razorpay.payment.gateway.dto;

public sealed interface PaymentResult permits
        PaymentResult.pending,
        PaymentResult.failure,
        PaymentResult.success
{
    record pending(String registrationReference) implements PaymentResult{}
    record failure(String errCode, String errorDescription) implements PaymentResult{}
    record success(String bankReference) implements  PaymentResult{}
}
