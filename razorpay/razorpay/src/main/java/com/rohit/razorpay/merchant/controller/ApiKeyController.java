package com.rohit.razorpay.merchant.controller;

import com.rohit.razorpay.merchant.dto.request.ApiKeyRequestDto;
import com.rohit.razorpay.merchant.dto.response.ApiKeyCreateResponseDto;
import com.rohit.razorpay.merchant.dto.response.ApiKeyResponseDto;
import com.rohit.razorpay.merchant.security.MerchantContext;
import com.rohit.razorpay.merchant.service.impl.ApiKeyServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/merchant/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {
    private final ApiKeyServiceImpl apiKeyService;
    private final MerchantContext merchantContext;

    @PostMapping("/create")
    public ResponseEntity<ApiKeyCreateResponseDto> create(@RequestBody @Valid ApiKeyRequestDto request){
        return ResponseEntity.status(HttpStatus.CREATED).body(apiKeyService.create(merchantContext.getMerchantId(),request));
    }

    @GetMapping()
    public ResponseEntity<List<ApiKeyResponseDto>> listByMerchant(){
        return ResponseEntity.status(HttpStatus.OK).body(apiKeyService.listByMerchant(merchantContext.getMerchantId()));
    }

    @DeleteMapping("/{keyId}")
    public ResponseEntity<Void> revoke(@PathVariable UUID keyId){
        apiKeyService.revoke(merchantContext.getMerchantId(), keyId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{keyId}/rotate")
    public ResponseEntity<ApiKeyCreateResponseDto> rotate(@PathVariable UUID keyId){
        return ResponseEntity.ok(apiKeyService.rotate(merchantContext.getMerchantId(),keyId));
    }
}
