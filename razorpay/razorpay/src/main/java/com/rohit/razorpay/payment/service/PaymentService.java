package com.rohit.razorpay.payment.service;

import com.rohit.razorpay.payment.dto.request.PaymentInitRequestDto;
import com.rohit.razorpay.payment.dto.response.PaymentResponseDto;

import java.util.UUID;

public interface PaymentService {
    PaymentResponseDto initiate(UUID merchantId, PaymentInitRequestDto request);
    PaymentResponseDto capture(UUID merchantId, UUID paymentId);
    void resolveAuthorization(UUID paymentId, boolean approve, String bankRef,String errorCode,String errorDescription);
}
