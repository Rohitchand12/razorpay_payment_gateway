package com.rohit.razorpay.payment.gateway.adapter;

import com.rohit.razorpay.payment.gateway.PaymentGatewayAdapter;
import com.rohit.razorpay.payment.gateway.dto.PaymentRequest;
import com.rohit.razorpay.payment.gateway.dto.PaymentResult;
import com.rohit.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.rohit.razorpay.vault.service.VaultCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class CardAdapter implements PaymentGatewayAdapter {

    private final VaultCardService vaultCardService;

    @Override
    public PaymentResult initiate(PaymentRequest request) {
        String token = request.methodDetails().get("token").toString();

        PaymentProcessorResponse response = vaultCardService.charge(
                token,request.paymentId(),request.amount(),request.methodDetails()
        );

        return switch (response){
            case PaymentProcessorResponse.failure failure -> new PaymentResult.failure(failure.errorCode(),failure.errorDescription());
            case PaymentProcessorResponse.pending pending -> new PaymentResult.pending(pending.paymentProcessorReference());
            case PaymentProcessorResponse.success success -> new PaymentResult.success(success.bankReference());
        };
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return null;
    }
}
