package com.rohit.razorpay.merchant.service.impl;

import com.rohit.razorpay.common.dto.WebhookTargetDto;
import com.rohit.razorpay.common.exceptions.ResourceNotFoundException;
import com.rohit.razorpay.common.utils.RandomizerUtil;
import com.rohit.razorpay.merchant.api.MerchantWebhookApi;
import com.rohit.razorpay.merchant.dto.request.WebhookRequestDto;
import com.rohit.razorpay.merchant.dto.response.WebhookResponseDto;
import com.rohit.razorpay.merchant.entity.MerchantEntity;
import com.rohit.razorpay.merchant.entity.WebhookConfigEntity;
import com.rohit.razorpay.merchant.mapper.WebhookMapper;
import com.rohit.razorpay.merchant.repository.MerchantRepository;
import com.rohit.razorpay.merchant.repository.WebhookRepository;
import com.rohit.razorpay.merchant.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookServiceImpl implements WebhookService, MerchantWebhookApi {

    private final WebhookRepository webhookRepository;
    private final MerchantRepository merchantRepository;

    @Qualifier("masterKeyEncryptor")
    private final BytesEncryptor bytesEncryptor;

    private final WebhookMapper webhookMapper;

    @Override
    public WebhookResponseDto create(UUID merchantId, WebhookRequestDto request) {
        MerchantEntity merchant = merchantRepository.findById(merchantId)
                .orElseThrow(()->new ResourceNotFoundException("Merchant",merchantId));

        String rawSecret = RandomizerUtil.randomBase64(32);
        byte[] rawSecretBytes = rawSecret.getBytes(StandardCharsets.UTF_8);
        String encodedRawSecret = Base64.getEncoder().encodeToString(bytesEncryptor.encrypt(rawSecretBytes));

        WebhookConfigEntity webhookConfig = WebhookConfigEntity.builder()
                .merchant(merchant)
                .webhookSecret(encodedRawSecret)
                .targetUrl(request.targetUrl())
                .eventTypes(request.eventTypes())
                .enabled(true)
                .build();
        webhookConfig = webhookRepository.save(webhookConfig);

        return webhookMapper.toWebhookResponseDto(webhookConfig,rawSecret);
    }

    @Override
    public List<WebhookResponseDto> list(UUID merchantId) {
        return webhookRepository.findByMerchant_Id(merchantId)
                .stream()
                .map((WebhookConfigEntity webhook)->webhookMapper.toWebhookResponseDto(webhook,null))
                .toList();
    }

    @Override
    public WebhookResponseDto getById(UUID merchantId, UUID webhookId) {
        WebhookConfigEntity webhook = getWebhookConfig(merchantId, webhookId);
        return webhookMapper.toWebhookResponseDto(webhook,null);
    }

    @Override
    @Transactional
    public WebhookResponseDto update(UUID merchantId, UUID webhookId, WebhookRequestDto request) {
        WebhookConfigEntity webhook = getWebhookConfig(merchantId, webhookId);
        webhook.setTargetUrl(request.targetUrl());
        webhook.setEventTypes(request.eventTypes());
        return webhookMapper.toWebhookResponseDto(webhook,null);
    }

    @Override
    @Transactional
    public void delete(UUID merchantId, UUID webhookId) {
        WebhookConfigEntity webhook = getWebhookConfig(merchantId, webhookId);
        webhookRepository.delete(webhook);
    }

    private WebhookConfigEntity getWebhookConfig(UUID merchantId, UUID webhookId){
        return webhookRepository.findByIdAndMerchant_Id(webhookId,merchantId)
                .orElseThrow(()-> new ResourceNotFoundException("Webhook",webhookId));
    }

    @Override
    public List<WebhookTargetDto> getActiveConfigsForEvent(UUID merchantId, String event) {
        return webhookRepository.findByMerchant_IdAndEnabledTrue(merchantId)
                .stream()
                .filter((WebhookConfigEntity webhook)->webhook.isSubscribedTo(event))
                .map((WebhookConfigEntity webhook)-> {
                    byte[] decryptedSecretBytes = bytesEncryptor.decrypt(webhook.getWebhookSecret().getBytes(StandardCharsets.UTF_8));
                    return new WebhookTargetDto(webhook.getId()
                            ,webhook.getTargetUrl(), new String(decryptedSecretBytes,StandardCharsets.UTF_8));
                })
                .toList();
    }
}
