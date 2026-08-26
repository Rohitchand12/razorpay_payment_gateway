package com.rohit.razorpay.payment.repository;

import com.rohit.razorpay.payment.entity.OrderRecordEntity;
import jakarta.persistence.LockModeType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderRecordEntity, UUID> {
    boolean existsByMerchantIdAndReceipt(UUID merchantId,String receipt);
    Optional<OrderRecordEntity> findByIdAndMerchantId(UUID id,UUID merchantId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from OrderRecordEntity o where o.id = :uuid and o.merchantId = :merchantId")
    Optional<OrderRecordEntity> findByIdAndMerchantIdForUpdate( UUID uuid, UUID merchantId);
}
