package com.rohit.razorpay.payment.gateway;

import com.rohit.razorpay.payment.gateway.dto.PaymentRequest;
import com.rohit.razorpay.payment.gateway.dto.PaymentResult;

public interface PaymentGatewayAdapter {
    PaymentResult initiate(PaymentRequest request);
}
