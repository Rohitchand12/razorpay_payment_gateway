package com.rohit.razorpay.merchant.mapper;

import com.rohit.razorpay.merchant.dto.response.WebhookResponseDto;
import com.rohit.razorpay.merchant.entity.WebhookConfigEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WebhookMapper {
    @Mapping(target = "webhookSecret", source = "rawSecret")
    WebhookResponseDto toWebhookResponseDto(WebhookConfigEntity webhookConfig,String rawSecret);
}
