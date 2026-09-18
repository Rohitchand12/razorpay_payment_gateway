package com.rohit.razorpay.operations.repository;

import com.rohit.razorpay.operations.entity.DlqEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DlqEventRepository extends JpaRepository<DlqEventEntity, UUID> {
}
