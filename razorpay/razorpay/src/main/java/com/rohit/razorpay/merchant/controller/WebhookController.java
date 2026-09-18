package com.rohit.razorpay.merchant.controller;

import com.rohit.razorpay.merchant.dto.request.WebhookRequestDto;
import com.rohit.razorpay.merchant.dto.response.WebhookResponseDto;
import com.rohit.razorpay.merchant.security.MerchantContext;
import com.rohit.razorpay.merchant.service.impl.WebhookServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/merchant/webhook")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final WebhookServiceImpl webhookService;
    private final MerchantContext merchantContext;

    // Create webhook
    @PostMapping
    public ResponseEntity<WebhookResponseDto> create(@RequestBody @Valid WebhookRequestDto request) {
        UUID merchantId = merchantContext.getMerchantId();
        log.info("Creating webhook for merchant {}", merchantId);
        WebhookResponseDto response = webhookService.create(merchantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // List all webhooks for merchant
    @GetMapping
    public ResponseEntity<List<WebhookResponseDto>> list() {
        UUID merchantId = merchantContext.getMerchantId();
        log.info("Listing webhooks for merchant {}", merchantId);
        return ResponseEntity.ok(webhookService.list(merchantId));
    }

    // Get webhook by ID
    @GetMapping("/{id}")
    public ResponseEntity<WebhookResponseDto> getById(@PathVariable UUID id) {
        UUID merchantId = merchantContext.getMerchantId();
        log.info("Fetching webhook {} for merchant {}", id, merchantId);
        return ResponseEntity.ok(webhookService.getById(merchantId, id));
    }

    // Update webhook
    @PutMapping("/{id}")
    public ResponseEntity<WebhookResponseDto> update(@PathVariable UUID id,
                                                     @RequestBody @Valid WebhookRequestDto request) {
        UUID merchantId = merchantContext.getMerchantId();
        log.info("Updating webhook {} for merchant {}", id, merchantId);
        return ResponseEntity.ok(webhookService.update(merchantId, id, request));
    }

    // Delete webhook
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID merchantId = merchantContext.getMerchantId();
        log.info("Deleting webhook {} for merchant {}", id, merchantId);
        webhookService.delete(merchantId, id);
        return ResponseEntity.noContent().build();
    }
}
