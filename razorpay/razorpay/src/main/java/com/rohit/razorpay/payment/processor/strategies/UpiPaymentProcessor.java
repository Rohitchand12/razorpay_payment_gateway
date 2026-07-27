package com.rohit.razorpay.payment.processor.strategies;

import com.rohit.razorpay.common.utils.RandomizerUtil;
import com.rohit.razorpay.payment.processor.PaymentProcessor;
import com.rohit.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.rohit.razorpay.payment.processor.dto.PaymentProcessorResponse;

public class UpiPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        final String VPA_CODE_FAIL = "BANK_CODE_FAIL";

        String vpaCode = request.methodDetails() != null ? request.methodDetails().get("vpa").toString() : null;

        //simulation

        if(VPA_CODE_FAIL.equals((vpaCode))){
            return new PaymentProcessorResponse.failure("UPI_REJECTED",
                    "Bank rejected the transaction"
            );
        }
        String processorRef = "UPI_PROCESSOR_"+ RandomizerUtil.randomBase64(16);


        return new PaymentProcessorResponse.pending(processorRef);
    }
}
