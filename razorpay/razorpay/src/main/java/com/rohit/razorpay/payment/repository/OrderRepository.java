package com.rohit.razorpay.payment.repository;

import com.rohit.razorpay.payment.entity.OrderRecordEntity;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderRecordEntity, UUID> {
    boolean existsByMerchantIdAndReceipt(UUID merchantId,String receipt);
}
