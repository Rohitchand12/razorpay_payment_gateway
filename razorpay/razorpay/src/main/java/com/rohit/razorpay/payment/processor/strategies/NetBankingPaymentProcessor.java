package com.rohit.razorpay.payment.processor.strategies;

import com.rohit.razorpay.common.utils.RandomizerUtil;
import com.rohit.razorpay.payment.processor.PaymentProcessor;
import com.rohit.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.rohit.razorpay.payment.processor.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NetBankingPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        final String BANK_CODE_FAIL = "BANK_CODE_FAIL";

        String bankCode = request.methodDetails() != null ? request.methodDetails().get("bank").toString() : null;

        //simulation

        if(BANK_CODE_FAIL.equals((bankCode))){
            return new PaymentProcessorResponse.failure("BANK_REJECTED",
                    "Bank rejected the transaction"
            );
        }
        String processorRef = "NBK_PROCESSOR_"+ RandomizerUtil.randomBase64(16);


        return new PaymentProcessorResponse.pending(processorRef);
    }
}
