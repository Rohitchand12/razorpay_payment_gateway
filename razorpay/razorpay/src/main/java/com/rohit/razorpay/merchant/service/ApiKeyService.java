package com.rohit.razorpay.merchant.service;

import com.rohit.razorpay.merchant.dto.request.ApiKeyRequestDto;
import com.rohit.razorpay.merchant.dto.response.ApiKeyCreateResponseDto;
import com.rohit.razorpay.merchant.dto.response.ApiKeyResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface ApiKeyService {
     ApiKeyCreateResponseDto create(UUID merchantId, ApiKeyRequestDto request);
     List<ApiKeyResponseDto> listByMerchant(UUID merchantId);
     void revoke(UUID merchantId, UUID keyId);
     ApiKeyCreateResponseDto rotate(UUID merchantId, UUID keyId);
}
