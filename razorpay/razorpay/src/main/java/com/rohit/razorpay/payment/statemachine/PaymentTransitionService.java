package com.rohit.razorpay.payment.statemachine;

import com.rohit.razorpay.common.enums.PaymentActor;
import com.rohit.razorpay.common.enums.PaymentEvent;
import com.rohit.razorpay.common.enums.PaymentStatus;
import com.rohit.razorpay.payment.entity.PaymentEntity;
import com.rohit.razorpay.payment.entity.PaymentTransitionLogEntity;
import com.rohit.razorpay.payment.repository.PaymentTransitionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentTransitionService {

    private final PaymentTransitionLogRepository paymentTransitionLogRepository;
    private final PaymentStateMachine stateMachine;

    public PaymentStatus apply(PaymentEntity payment, PaymentEvent event){

        PaymentStatus next = stateMachine.transition(payment.getStatus(),event);

        PaymentTransitionLogEntity paymentTransitionLog = PaymentTransitionLogEntity.builder()
                .eventType(event)
                .fromStatus(payment.getStatus().toString())
                .toStatus(next.toString())
                .occurredAt(LocalDateTime.now())
                .actor(PaymentActor.SYSTEM)
                .payment(payment)
                .reason(event.toString())
                .build();
        paymentTransitionLogRepository.save(paymentTransitionLog);
        payment.setStatus(next);
        return next;
    }
}
