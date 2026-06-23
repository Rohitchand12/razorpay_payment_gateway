package com.rohit.razorpay.merchant.repository;

import com.rohit.razorpay.merchant.entity.ApiKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKeyEntity, UUID> {
    public List<ApiKeyEntity> findAllByMerchant_Id(UUID merchantId);
}
