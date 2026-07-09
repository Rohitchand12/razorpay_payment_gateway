package com.rohit.razorpay.merchant.mapper;

import com.rohit.razorpay.merchant.dto.response.ApiKeyCreateResponseDto;
import com.rohit.razorpay.merchant.dto.response.ApiKeyResponseDto;
import com.rohit.razorpay.merchant.entity.ApiKeyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ApiKeyMapper {
    ApiKeyCreateResponseDto toApiKeyCreateResponseDto(ApiKeyEntity apiKey);

    ApiKeyResponseDto toApiKeyResponseDto(ApiKeyEntity apiKey);

    List<ApiKeyResponseDto> toApiKeyResponseDtoList(List<ApiKeyEntity> apiKeys);
}
