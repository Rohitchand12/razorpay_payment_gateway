package com.rohit.razorpay.payment.repository;

import com.rohit.razorpay.common.enums.PaymentStatus;
import com.rohit.razorpay.payment.entity.PaymentEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity,UUID> {
    List<PaymentEntity> findByOrder_Id(UUID merchantId, UUID orderId);
    Optional<PaymentEntity> findByIdAndMerchantId(UUID paymentId, UUID merchantId);

    List<PaymentEntity> findByStatusAndCreatedAtBefore(PaymentStatus paymentStatus, LocalDateTime timeBefore);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PaymentEntity p where p.id = :paymentId and p.merchantId = :merchantId")
    Optional<PaymentEntity> findByIdAndMerchantIdForUpdate(UUID paymentId, UUID merchantId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PaymentEntity p where id = :paymentId")
    Optional<PaymentEntity> findByIdForUpdate(UUID paymentId);
}
