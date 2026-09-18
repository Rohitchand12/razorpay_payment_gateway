package com.rohit.razorpay.merchant.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record WebhookRequestDto(

        @NotBlank
        @Size(max = 500)
        @Pattern(regexp = "https?://.+",message = "Webhook url must be a valid http(s) url")
        String targetUrl,

        //Comma separated event types ex PAYMENT_STATUS_CHANGED,REFUND_SUCCESS
        //NULL, BLANK, ALL subscribes to every event type
        @Size(max = 1000)
        String eventTypes
) {
}
