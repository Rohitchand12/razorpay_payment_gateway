package com.rohit.razorpay.vault.repository;

import com.rohit.razorpay.vault.entity.VaultCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VaultCardRepository extends JpaRepository<VaultCardEntity, UUID> {
}
