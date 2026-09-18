package com.rohit.razorpay.common.dto;

import java.util.UUID;

public record WebhookTargetDto(UUID configId, String targetUrl, String webhookSecret) {
}
