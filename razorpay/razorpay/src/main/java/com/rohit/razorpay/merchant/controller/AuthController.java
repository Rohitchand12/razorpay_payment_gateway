package com.rohit.razorpay.merchant.controller;

import com.rohit.razorpay.merchant.dto.request.MerchantSignupRequestDto;
import com.rohit.razorpay.merchant.dto.response.MerchantResponseDto;
import com.rohit.razorpay.merchant.service.impl.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/signup")
    ResponseEntity<MerchantResponseDto> signup(@RequestBody @Validated MerchantSignupRequestDto request){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(request));
    }
}
