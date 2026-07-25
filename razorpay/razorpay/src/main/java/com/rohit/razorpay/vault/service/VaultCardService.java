package com.rohit.razorpay.vault.service;

import com.rohit.razorpay.vault.dto.request.TokenizeRequestDto;
import com.rohit.razorpay.vault.dto.response.TokenizeResponseDto;

import java.util.UUID;

public interface VaultCardService {
    TokenizeResponseDto tokenize(TokenizeRequestDto requestDto, UUID merchantId);
}
