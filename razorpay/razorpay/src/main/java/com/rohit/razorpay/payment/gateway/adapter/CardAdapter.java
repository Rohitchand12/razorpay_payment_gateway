package com.rohit.razorpay.payment.gateway.adapter;

import com.rohit.razorpay.payment.gateway.PaymentGatewayAdapter;
import com.rohit.razorpay.payment.gateway.dto.PaymentRequest;
import com.rohit.razorpay.payment.gateway.dto.PaymentResult;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CardAdapter implements PaymentGatewayAdapter {
    @Override
    public PaymentResult initiate(PaymentRequest request) {
        return null;
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return null;
    }
}
