package com.rohit.razorpay.merchant.dto.response;

import com.rohit.razorpay.common.enums.BusinessType;
import com.rohit.razorpay.common.enums.MerchantStatus;

import java.util.UUID;

public record MerchantResponseDto(
        UUID id,
        String name,
        String email,
        String businessName,
        BusinessType businessType,
        MerchantStatus merchantStatus
) {

}
