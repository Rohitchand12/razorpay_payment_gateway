package com.rohit.razorpay.merchant.repository;

import com.rohit.razorpay.merchant.entity.WebhookConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WebhookRepository extends JpaRepository<WebhookConfigEntity, UUID> {
    List<WebhookConfigEntity> findByMerchant_Id(UUID merchantId);

    Optional<WebhookConfigEntity> findByIdAndMerchant_Id(UUID webhookId, UUID merchantId);

    List<WebhookConfigEntity> findByMerchant_IdAndEnabledTrue(UUID merchantId);
}
