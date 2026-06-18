package com.rohit.razorpay.merchant.service;

import com.rohit.razorpay.merchant.dto.request.MerchantSignupRequestDto;
import com.rohit.razorpay.merchant.dto.response.MerchantResponseDto;

public interface AuthService {
    MerchantResponseDto signup(MerchantSignupRequestDto request);
}
