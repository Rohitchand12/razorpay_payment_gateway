package com.rohit.razorpay.merchant.controller;

import com.rohit.razorpay.merchant.dto.request.ApiKeyRequestDto;
import com.rohit.razorpay.merchant.dto.response.ApiKeyCreateResponseDto;
import com.rohit.razorpay.merchant.dto.response.ApiKeyResponseDto;
import com.rohit.razorpay.merchant.service.impl.ApiKeyServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/merchants/{merchantId}/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {
    private final ApiKeyServiceImpl apiKeyService;

    @PostMapping("/create")
    public ResponseEntity<ApiKeyCreateResponseDto> create(@PathVariable UUID merchantId, @RequestBody @Valid ApiKeyRequestDto request){
        return ResponseEntity.status(HttpStatus.CREATED).body(apiKeyService.create(merchantId,request));
    }

    @GetMapping()
    public ResponseEntity<List<ApiKeyResponseDto>> listByMerchant(@PathVariable UUID merchantId){
        return ResponseEntity.status(HttpStatus.OK).body(apiKeyService.listByMerchant(merchantId));
    }

    @DeleteMapping("/{keyId}")
    public ResponseEntity<Void> revoke(@PathVariable UUID merchantId, @PathVariable UUID keyId){
        apiKeyService.revoke(merchantId, keyId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{keyId}/rotate")
    public ResponseEntity<ApiKeyCreateResponseDto> rotate(@PathVariable UUID merchantId, @PathVariable UUID keyId){
        return ResponseEntity.ok(apiKeyService.rotate(merchantId,keyId));
    }
}
