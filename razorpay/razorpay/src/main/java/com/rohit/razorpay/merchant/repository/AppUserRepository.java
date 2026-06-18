package com.rohit.razorpay.merchant.repository;

import com.rohit.razorpay.merchant.entity.AppUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository<AppUserEntity, UUID> {
}
