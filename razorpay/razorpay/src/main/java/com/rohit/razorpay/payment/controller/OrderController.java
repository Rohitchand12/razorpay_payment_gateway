package com.rohit.razorpay.payment.controller;

import com.rohit.razorpay.payment.dto.request.OrderCreateRequestDto;
import com.rohit.razorpay.payment.dto.response.OrderResponseDto;
import com.rohit.razorpay.payment.service.impl.OrderServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderServiceImpl orderService;

    UUID merchantId = UUID.fromString("accca886-9b0a-4a05-910f-a9b8f475335f");

    @PostMapping()
    public ResponseEntity<OrderResponseDto> create(@RequestBody @Valid  OrderCreateRequestDto request){
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(merchantId,request));
    }

    //get by id
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getById(@PathVariable UUID id){
        return ResponseEntity.ok(orderService.getById(merchantId,id));
    }

    //cancel order


    //list payments

}
