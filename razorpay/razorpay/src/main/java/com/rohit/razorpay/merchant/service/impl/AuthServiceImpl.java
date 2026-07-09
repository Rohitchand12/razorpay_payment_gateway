package com.rohit.razorpay.merchant.service.impl;

import com.rohit.razorpay.common.enums.MerchantStatus;
import com.rohit.razorpay.common.enums.UserRole;
import com.rohit.razorpay.common.exceptions.DuplicateResourceException;
import com.rohit.razorpay.merchant.dto.request.MerchantSignupRequestDto;
import com.rohit.razorpay.merchant.dto.response.MerchantResponseDto;
import com.rohit.razorpay.merchant.entity.AppUserEntity;
import com.rohit.razorpay.merchant.entity.MerchantEntity;
import com.rohit.razorpay.merchant.mapper.MerchantMapper;
import com.rohit.razorpay.merchant.repository.AppUserRepository;
import com.rohit.razorpay.merchant.repository.MerchantRepository;
import com.rohit.razorpay.merchant.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository appUserRepository;
    private final MerchantRepository merchantRepository;
    private final MerchantMapper merchantMapper;

    @Override
    @Transactional
    public MerchantResponseDto signup(MerchantSignupRequestDto request){
        //check if the merchant already exists
        if(merchantRepository.existsByEmail(request.email())){
            throw new DuplicateResourceException("DUPLICATE_MERCHANT_EMAIL","Merchant with this email already exists "+ request.email());
        }
        //BUSINESS LOGIC

        //Create a merchant
        MerchantEntity merchant = merchantMapper.fromSignupRequestToMerchantEntity(request);
        merchant.setMerchantStatus(MerchantStatus.PENDING_KYC);
        merchant = merchantRepository.save(merchant);

        //Create an app user that is the main owner
        AppUserEntity appUser = AppUserEntity.builder()
                .role(UserRole.OWNER)
                .merchant(merchant)
                .email(request.email())
                .password_hash(request.password()) //TODO: Encrypt this
                .build();

        appUserRepository.save(appUser);

        return merchantMapper.toMerchantResponseDto(merchant); //return a dto
    }
}
