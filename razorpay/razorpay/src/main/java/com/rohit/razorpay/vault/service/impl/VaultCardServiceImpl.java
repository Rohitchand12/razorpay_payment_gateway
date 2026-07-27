package com.rohit.razorpay.vault.service.impl;

import com.rohit.razorpay.common.entity.Money;
import com.rohit.razorpay.common.enums.CardBrand;
import com.rohit.razorpay.common.exceptions.ResourceNotFoundException;
import com.rohit.razorpay.common.utils.RandomizerUtil;
import com.rohit.razorpay.payment.processor.PaymentProcessorRouter;
import com.rohit.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.rohit.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.rohit.razorpay.vault.config.VaultEncryptionConfig;
import com.rohit.razorpay.vault.dto.request.TokenizeRequestDto;
import com.rohit.razorpay.vault.dto.response.TokenizeResponseDto;
import com.rohit.razorpay.vault.entity.CardTokenEntity;
import com.rohit.razorpay.vault.entity.VaultCardEntity;
import com.rohit.razorpay.vault.repository.CardTokenRepository;
import com.rohit.razorpay.vault.repository.VaultCardRepository;
import com.rohit.razorpay.vault.service.VaultCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VaultCardServiceImpl implements VaultCardService {

    private final VaultCardRepository vaultCardRepository;
    private final CardTokenRepository cardTokenRepository;
    private final BytesEncryptor dekEncryptor;
    private final PaymentProcessorRouter paymentProcessorRouter;

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

    @Override
    public PaymentProcessorResponse charge(String token,
                                           UUID paymentId, Money amount, Map<String, Object> methodDetails){
        CardTokenEntity cardToken = cardTokenRepository.findByTokenAndRevokedAtIsNull(token)
                .orElseThrow(()-> new ResourceNotFoundException("CardToken",token));
        VaultCardEntity vaultCard = cardToken.getVaultCard();
        //need to get the real pan. Get the encrypted dek, decrypt to get original dek, using original dek
        //decrypt the encrypted pan
        byte[] panBytes = null;
        try{
            byte[] dek = dekEncryptor.decrypt(vaultCard.getEncryptedDek());
            panBytes = VaultEncryptionConfig.panEncryptor(dek).decrypt(vaultCard.getEncryptedPan());
            String pan = new String(panBytes,StandardCharsets.UTF_8);
            String expiry = vaultCard.getExpiryMonth()+"/"+vaultCard.getExpiryYear();

            PaymentProcessorRequest request = PaymentProcessorRequest.card(
                    paymentId,
                    amount,
                    pan,
                    expiry,
                    methodDetails
            );

            PaymentProcessorResponse response = paymentProcessorRouter.charge(request);
            log.info("Vault charge registered, token = {}********",token.substring(0,4));
            return response;
        }catch (Exception e){
            log.warn("Vault charge registered, token = {}********",token.substring(0,4));
            return new PaymentProcessorResponse.failure("VAULT_CHARGE_FAILED","Vault charge has failed");
        }finally{
            if(panBytes != null) Arrays.fill(panBytes,(byte) 0);
        }
    }
    private CardBrand detectBrand(String pan) {
        if(pan.startsWith("4")) return  CardBrand.VISA;
        else if(pan.startsWith("5") || pan.startsWith("2")) return CardBrand.MASTERCARD;
        else if(pan.startsWith("37") || pan.startsWith("34")) return CardBrand.AMEX;
        else return CardBrand.RUPAY;
    }
}
