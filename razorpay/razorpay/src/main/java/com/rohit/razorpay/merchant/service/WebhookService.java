package com.rohit.razorpay.merchant.service;

import com.rohit.razorpay.merchant.dto.request.WebhookRequestDto;
import com.rohit.razorpay.merchant.dto.response.WebhookResponseDto;

import java.util.List;
import java.util.UUID;

public interface WebhookService {
    WebhookResponseDto create(UUID merchantId, WebhookRequestDto request);

    List<WebhookResponseDto> list(UUID merchantId);

    WebhookResponseDto getById(UUID merchantId, UUID webhookId);

    WebhookResponseDto update(UUID merchantId, UUID webhookId,  WebhookRequestDto request);

    void delete(UUID merchantId, UUID webhookId);
}
