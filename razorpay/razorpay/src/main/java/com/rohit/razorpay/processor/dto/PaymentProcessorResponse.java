package com.rohit.razorpay.processor.dto;

public sealed interface PaymentProcessorResponse permits
        PaymentProcessorResponse.pending,
        PaymentProcessorResponse.success,
        PaymentProcessorResponse.failure {
    record pending(String paymentProcessorReference) implements PaymentProcessorResponse{}
    record success(String paymentProcessorReference, String bankReference) implements PaymentProcessorResponse{}
    record failure(String errorCode, String errorDescription) implements PaymentProcessorResponse{}
}
