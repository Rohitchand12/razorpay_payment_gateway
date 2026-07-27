package com.rohit.razorpay.vault.service;

import com.rohit.razorpay.common.entity.Money;
import com.rohit.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.rohit.razorpay.vault.dto.request.TokenizeRequestDto;
import com.rohit.razorpay.vault.dto.response.TokenizeResponseDto;

import java.util.Map;
import java.util.UUID;

public interface VaultCardService {
    TokenizeResponseDto tokenize(TokenizeRequestDto requestDto, UUID merchantId);
    PaymentProcessorResponse charge(String token,
                                    UUID paymentId, Money amount, Map<String, Object> methodDetails);
}
