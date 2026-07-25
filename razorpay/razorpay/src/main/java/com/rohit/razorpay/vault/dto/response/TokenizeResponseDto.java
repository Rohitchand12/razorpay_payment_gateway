package com.rohit.razorpay.vault.dto.response;

import com.rohit.razorpay.common.enums.CardBrand;

public record TokenizeResponseDto(
    String token,
    String lastFour,
    CardBrand brand,
    Integer expiryYear,
    Integer expiryMonth
) {
}
