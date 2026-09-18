package com.rohit.razorpay.merchant.api;

import com.rohit.razorpay.common.dto.WebhookTargetDto;

import java.util.List;
import java.util.UUID;

public interface MerchantWebhookApi {
    List<WebhookTargetDto> getActiveConfigsForEvent(UUID merchantId, String event);
}
