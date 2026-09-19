package com.rohit.razorpay.merchant.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/webhook")
public class WebhookTargetTestController {
    @PostMapping("/success")
    public ResponseEntity<Void> success(@RequestBody Map<String,Object> body){
        return ResponseEntity.noContent().build();
    }
}
