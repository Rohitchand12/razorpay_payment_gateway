package com.rohit.razorpay.processor;

import com.rohit.razorpay.processor.dto.PaymentProcessorRequest;
import com.rohit.razorpay.processor.dto.PaymentProcessorResponse;

public interface PaymentProcessor {
    PaymentProcessorResponse charge(PaymentProcessorRequest request);
}
