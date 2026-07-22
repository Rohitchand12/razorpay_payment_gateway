package com.rohit.razorpay.payment.processor;

import com.rohit.razorpay.common.enums.PaymentMethod;
import com.rohit.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.rohit.razorpay.payment.processor.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentProcessorRouter {

    private final Map<PaymentMethod, PaymentProcessor> paymentProcessorMap;

    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        PaymentProcessor paymentProcessor = paymentProcessorMap.get(request.method());
        if(paymentProcessor == null) {
            throw new IllegalArgumentException("No payment processor for method: " + request.method().name());
        }
        return paymentProcessor.charge(request);
    }
}
