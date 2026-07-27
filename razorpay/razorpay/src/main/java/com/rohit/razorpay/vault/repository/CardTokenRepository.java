package com.rohit.razorpay.vault.repository;

import com.rohit.razorpay.vault.entity.CardTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardTokenRepository extends JpaRepository<CardTokenEntity, UUID> {
    Optional<CardTokenEntity> findByTokenAndRevokedAtIsNull(String token);
}
