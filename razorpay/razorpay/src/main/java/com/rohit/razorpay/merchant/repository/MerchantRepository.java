package com.rohit.razorpay.merchant.repository;

import com.rohit.razorpay.merchant.entity.MerchantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MerchantRepository extends JpaRepository<MerchantEntity,UUID> {
    boolean existsByEmail(String email);
}
