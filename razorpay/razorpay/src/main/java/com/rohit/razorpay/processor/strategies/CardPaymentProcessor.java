package com.rohit.razorpay.processor.strategies;

import com.rohit.razorpay.processor.PaymentProcessor;
import com.rohit.razorpay.processor.dto.PaymentProcessorRequest;
import com.rohit.razorpay.processor.dto.PaymentProcessorResponse;

public class CardPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        return null;
    }
}
