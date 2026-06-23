package com.rohit.razorpay.payment.controller;

import com.rohit.razorpay.payment.dto.request.OrderCreateRequestDto;
import com.rohit.razorpay.payment.dto.response.OrderCreateResponseDto;
import com.rohit.razorpay.payment.service.impl.OrderServiceImpl;
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
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderServiceImpl orderService;

    UUID merchantId = UUID.fromString("839acca4-7ca1-4efb-ba29-9726e9048651");

    @PostMapping()
    public ResponseEntity<OrderCreateResponseDto> create(@RequestBody @Valid  OrderCreateRequestDto request){
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(merchantId,request));
    }

}
