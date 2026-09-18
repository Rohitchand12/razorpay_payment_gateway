package com.rohit.razorpay.operations.repository;

import com.rohit.razorpay.common.enums.WebhookEventStatus;
import com.rohit.razorpay.operations.entity.WebhookEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface WebhookEventRepository extends JpaRepository<WebhookEventEntity, UUID> {
    List<WebhookEventEntity> findByStatusAndNextRetryAtBefore(WebhookEventStatus webhookEventStatus, LocalDateTime now);
}
