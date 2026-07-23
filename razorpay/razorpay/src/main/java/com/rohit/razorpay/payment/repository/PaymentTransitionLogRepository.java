package com.rohit.razorpay.payment.repository;

import com.rohit.razorpay.payment.entity.PaymentTransitionLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentTransitionLogRepository extends JpaRepository<PaymentTransitionLogEntity, UUID> {
}
