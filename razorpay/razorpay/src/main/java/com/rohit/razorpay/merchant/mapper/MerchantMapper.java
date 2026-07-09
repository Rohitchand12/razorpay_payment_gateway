package com.rohit.razorpay.merchant.mapper;

import com.rohit.razorpay.merchant.dto.request.MerchantSignupRequestDto;
import com.rohit.razorpay.merchant.dto.response.MerchantResponseDto;
import com.rohit.razorpay.merchant.entity.MerchantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MerchantMapper {
    MerchantEntity fromSignupRequestToMerchantEntity(MerchantSignupRequestDto merchant);

    MerchantResponseDto toMerchantResponseDto(MerchantEntity merchant);
}
