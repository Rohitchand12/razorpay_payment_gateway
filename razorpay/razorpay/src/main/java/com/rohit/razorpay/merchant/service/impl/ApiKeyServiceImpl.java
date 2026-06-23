package com.rohit.razorpay.merchant.service.impl;

import com.rohit.razorpay.common.enums.Environment;
import com.rohit.razorpay.common.exceptions.ResourceNotFoundException;
import com.rohit.razorpay.common.utils.RandomizerUtil;
import com.rohit.razorpay.merchant.dto.request.ApiKeyRequestDto;
import com.rohit.razorpay.merchant.dto.response.ApiKeyCreateResponseDto;
import com.rohit.razorpay.merchant.dto.response.ApiKeyResponseDto;
import com.rohit.razorpay.merchant.entity.ApiKeyEntity;
import com.rohit.razorpay.merchant.entity.MerchantEntity;
import com.rohit.razorpay.merchant.repository.ApiKeyRepository;
import com.rohit.razorpay.merchant.repository.MerchantRepository;
import com.rohit.razorpay.merchant.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final MerchantRepository merchantRepository;

    @Override
    @Transactional
    public ApiKeyCreateResponseDto create(UUID merchantId, ApiKeyRequestDto request){
        MerchantEntity merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() ->new ResourceNotFoundException("merchant",merchantId));

        //need to build the public key
        String key = "rzp_"+request.environment().toLowerCase()+"_"+ RandomizerUtil.randomBase64(24);
        //need to build the secret and hash it
        String keySecret = RandomizerUtil.randomBase64(40);

        ApiKeyEntity apiKey = ApiKeyEntity.builder()
                .merchant(merchant)
                .keyId(key)
                .keySecretHash(keySecret)
                .environment(Environment.valueOf(request.environment()))
                .build();

        apiKey = apiKeyRepository.save(apiKey);
        return new ApiKeyCreateResponseDto(
                apiKey.getId(),
                apiKey.getKeyId(),
                apiKey.getKeySecretHash(),
                apiKey.getEnvironment()
        );
    }

    @Override
    public List<ApiKeyResponseDto> listByMerchant(UUID merchantId){
        MerchantEntity merchant = merchantRepository
                .findById(merchantId)
                .orElseThrow(()->new ResourceNotFoundException("merchant",merchantId));

        List<ApiKeyEntity> apiKeyEntities = apiKeyRepository.findAllByMerchant_Id(merchantId);
        return apiKeyEntities.stream()
                .map((apiKeyEntity)-> new ApiKeyResponseDto(
                         apiKeyEntity.getId()
                        ,apiKeyEntity.getMerchant()
                        ,apiKeyEntity.getKeyId()
                        ,apiKeyEntity.getEnvironment()
                        ,apiKeyEntity.getEnabled()
                        ,apiKeyEntity.getLastUsedAt()
                ))
                .toList();
    }

    @Override
    @Transactional
    public void revoke(UUID merchantId, UUID keyId) {
        ApiKeyEntity apiKey = apiKeyRepository.findById(keyId)
                .filter((key)->key.getMerchant().getId().equals(merchantId))
                .orElseThrow(()->new ResourceNotFoundException("ApiKey",keyId));
        apiKey.setEnabled(false);
        apiKeyRepository.save(apiKey); //can avoid this is added @transactional  i.e. sees the dirty and saves
    }

    @Override
    @Transactional
    public ApiKeyCreateResponseDto rotate(UUID merchantId, UUID keyId) {
        ApiKeyEntity apiKey = apiKeyRepository.findById(keyId)
                .filter((key)->key.getMerchant().getId().equals(merchantId))
                .orElseThrow(()->new ResourceNotFoundException("ApiKey",keyId));

        String newRawSecret = RandomizerUtil.randomBase64(40);
        apiKey.setPrevKeySecretHash(apiKey.getKeySecretHash());
        apiKey.setKeySecretHash(newRawSecret);
        apiKey.setRotatedAt(LocalDateTime.now());
        apiKey.setGracePeriodExpiresAt(LocalDateTime.now().plusHours(24));

        apiKey = apiKeyRepository.save(apiKey);
        return new ApiKeyCreateResponseDto(
                 apiKey.getId()
                ,apiKey.getKeyId()
                ,apiKey.getKeySecretHash()
                ,apiKey.getEnvironment()
        );
    }
}
