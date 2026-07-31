package com.rohit.razorpay.merchant.service.impl;

import com.rohit.razorpay.common.enums.MerchantStatus;
import com.rohit.razorpay.common.enums.UserRole;
import com.rohit.razorpay.common.exceptions.DuplicateResourceException;
import com.rohit.razorpay.common.exceptions.ResourceNotFoundException;
import com.rohit.razorpay.merchant.dto.request.LoginRequestDto;
import com.rohit.razorpay.merchant.dto.request.MerchantSignupRequestDto;
import com.rohit.razorpay.merchant.dto.response.LoginResponseDto;
import com.rohit.razorpay.merchant.dto.response.MerchantResponseDto;
import com.rohit.razorpay.merchant.entity.AppUserEntity;
import com.rohit.razorpay.merchant.entity.MerchantEntity;
import com.rohit.razorpay.merchant.mapper.MerchantMapper;
import com.rohit.razorpay.merchant.repository.AppUserRepository;
import com.rohit.razorpay.merchant.repository.MerchantRepository;
import com.rohit.razorpay.merchant.security.JwtUtil;
import com.rohit.razorpay.merchant.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository appUserRepository;
    private final MerchantRepository merchantRepository;
    private final MerchantMapper merchantMapper;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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
                .passwordHash(passwordEncoder.encode(request.password()))
                .build();

        appUserRepository.save(appUser);

        return merchantMapper.toMerchantResponseDto(merchant); //return a dto
    }

    @Override
    public LoginResponseDto login(LoginRequestDto request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(), request.password()
                )
        );
        AppUserEntity appUser = (AppUserEntity) appUserRepository.findByEmail(request.email())
                .orElseThrow(()->new ResourceNotFoundException("AppUser", request.email()));
        String accessToken = jwtUtil.generateAccessToken(request.email(),appUser.getMerchant().getId(),appUser.getRole());

        return new LoginResponseDto(accessToken);
    }
}
