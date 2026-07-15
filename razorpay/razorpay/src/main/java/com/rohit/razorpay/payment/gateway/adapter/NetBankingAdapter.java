package com.rohit.razorpay.payment.gateway.adapter;

import com.rohit.razorpay.payment.gateway.PaymentGatewayAdapter;
import com.rohit.razorpay.payment.gateway.dto.PaymentRequest;
import com.rohit.razorpay.payment.gateway.dto.PaymentResult;

public class NetBankingAdapter implements PaymentGatewayAdapter {
    @Override
    public PaymentResult initiate(PaymentRequest request) {
        return null;
    }
}
