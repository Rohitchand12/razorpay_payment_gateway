package com.rohit.razorpay.payment.repository;

import com.rohit.razorpay.common.enums.OutboxStatus;
import com.rohit.razorpay.payment.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<Outbox, UUID> {
    List<Outbox> findByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
