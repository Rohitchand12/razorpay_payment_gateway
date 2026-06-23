package com.rohit.razorpay.merchant.dto.response;

import com.rohit.razorpay.common.enums.Environment;

import java.util.UUID;

public record ApiKeyCreateResponseDto(
        UUID id,
        String keyId,
        String keySecret,
        Environment environment
) {
}
