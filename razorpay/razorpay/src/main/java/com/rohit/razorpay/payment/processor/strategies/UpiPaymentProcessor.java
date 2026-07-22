package com.rohit.razorpay.payment.processor.strategies;

import com.rohit.razorpay.payment.processor.PaymentProcessor;
import com.rohit.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.rohit.razorpay.payment.processor.dto.PaymentProcessorResponse;

public class UpiPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        return null;
    }
}
