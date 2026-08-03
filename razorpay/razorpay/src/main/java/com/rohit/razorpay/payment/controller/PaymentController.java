package com.rohit.razorpay.payment.controller;

import com.rohit.razorpay.merchant.security.MerchantContext;
import com.rohit.razorpay.payment.dto.request.PaymentInitRequestDto;
import com.rohit.razorpay.payment.dto.response.PaymentResponseDto;
import com.rohit.razorpay.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final MerchantContext merchantContext;


    @PostMapping()
    public ResponseEntity<PaymentResponseDto> initiate(@Valid @RequestBody PaymentInitRequestDto request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.initiate(merchantContext.getMerchantId(),request));
    }

    @PostMapping("/{paymentId}/capture")
    public ResponseEntity<PaymentResponseDto> capture(@PathVariable UUID paymentId){
        return ResponseEntity.ok(paymentService.capture(merchantContext.getMerchantId(), paymentId));
    }


}
