package com.rohit.razorpay.merchant.repository;

import com.rohit.razorpay.merchant.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {
    Optional<CustomerEntity> findByMerchant_IdAndEmail(UUID merchantId, String email);
}
