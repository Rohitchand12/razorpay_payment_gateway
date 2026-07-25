package com.rohit.razorpay.vault.controller;

import com.rohit.razorpay.vault.dto.request.TokenizeRequestDto;
import com.rohit.razorpay.vault.dto.response.TokenizeResponseDto;
import com.rohit.razorpay.vault.service.VaultCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vault")
@RequiredArgsConstructor
public class VaultController {
    private final VaultCardService vaultCardService;

    UUID merchantId = UUID.fromString("839acca4-7ca1-4efb-ba29-9726e9048651");

    @PostMapping("/tokenize")
    public ResponseEntity<TokenizeResponseDto> tokenize(@Valid @RequestBody TokenizeRequestDto request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vaultCardService.tokenize(request,merchantId));
    }

}
