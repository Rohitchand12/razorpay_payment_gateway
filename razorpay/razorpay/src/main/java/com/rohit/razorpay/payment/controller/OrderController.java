package com.rohit.razorpay.payment.controller;

import com.rohit.razorpay.merchant.security.MerchantContext;
import com.rohit.razorpay.payment.dto.request.OrderCreateRequestDto;
import com.rohit.razorpay.payment.dto.response.OrderResponseDto;
import com.rohit.razorpay.payment.service.impl.OrderServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@Slf4j
@RequiredArgsConstructor
public class OrderController {
    private final OrderServiceImpl orderService;
    private final MerchantContext merchantContext;

    @PostMapping()
    public ResponseEntity<OrderResponseDto> create(@RequestBody @Valid  OrderCreateRequestDto request){
        log.info("merchant id: {}", merchantContext.getMerchantId());
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(merchantContext.getMerchantId(),request));
    }

    //get by id
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getById(@PathVariable UUID id){
        return ResponseEntity.ok(orderService.getById(merchantContext.getMerchantId(),id));
    }

    //cancel order


    //list payments

}
