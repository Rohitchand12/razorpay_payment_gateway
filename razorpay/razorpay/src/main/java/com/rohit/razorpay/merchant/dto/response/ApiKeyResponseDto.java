package com.rohit.razorpay.merchant.dto.response;

import com.rohit.razorpay.common.enums.Environment;
import com.rohit.razorpay.merchant.entity.MerchantEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApiKeyResponseDto(
    UUID id,
    MerchantEntity merchant,
    String keyId,
    Environment environment,
    Boolean enabled,
    LocalDateTime lastUsedAt
) {
}
