package com.rohit.razorpay.vault.service.impl;

import com.rohit.razorpay.common.enums.CardBrand;
import com.rohit.razorpay.common.utils.RandomizerUtil;
import com.rohit.razorpay.vault.config.VaultEncryptionConfig;
import com.rohit.razorpay.vault.dto.request.TokenizeRequestDto;
import com.rohit.razorpay.vault.dto.response.TokenizeResponseDto;
import com.rohit.razorpay.vault.entity.CardTokenEntity;
import com.rohit.razorpay.vault.entity.VaultCardEntity;
import com.rohit.razorpay.vault.repository.CardTokenRepository;
import com.rohit.razorpay.vault.repository.VaultCardRepository;
import com.rohit.razorpay.vault.service.VaultCardService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.LuhnCheck;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VaultCardServiceImpl implements VaultCardService {

    private final VaultCardRepository vaultCardRepository;
    private final CardTokenRepository cardTokenRepository;
    private final BytesEncryptor dekEncryptor;

    @Override
    @Transactional
    public TokenizeResponseDto tokenize(TokenizeRequestDto request, UUID merchantId) {
        String lastFour = request.pan().substring(request.pan().length()-4);
        String bin = request.pan().substring(0,6);
        CardBrand cardBrand = detectBrand(request.pan());
        //the pan is encrypted using a dek
        byte[] dek = KeyGenerators.secureRandom(32).generateKey();
        byte[] encryptedPan = VaultEncryptionConfig
                .panEncryptor(dek)
                .encrypt(request.pan().getBytes(StandardCharsets.UTF_8));
        //we store the dek also in our database by encrypting dek using a master key
        byte[] encryptedDek = dekEncryptor.encrypt(dek);

        VaultCardEntity vaultCard = vaultCardRepository.save(VaultCardEntity.builder()
                .brand(cardBrand)
                .bin(bin)
                .expiryMonth(request.expiryMonth().toString())
                .expiryYear(request.expiryYear().toString())
                .encryptedDek(encryptedDek)
                .encryptedPan(encryptedPan)
                .cardHolderName(request.cardHolderName())
                .build());

        String token = "tok_"+ RandomizerUtil.randomBase64(32);

        CardTokenEntity cardToken = cardTokenRepository.save(CardTokenEntity.builder()
                .token(token)
                .vaultCard(vaultCard)
                .merchantId(merchantId)
                .customerId(request.customerId())
                .build());

        return null;
    }

    private CardBrand detectBrand(String pan) {
        if(pan.startsWith("4")) return  CardBrand.VISA;
        else if(pan.startsWith("5") || pan.startsWith("2")) return CardBrand.MASTERCARD;
        else if(pan.startsWith("37") || pan.startsWith("34")) return CardBrand.AMEX;
        else return CardBrand.RUPAY;
    }
}
