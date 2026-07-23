package com.rohit.razorpay.payment.statemachine;

import com.rohit.razorpay.common.enums.PaymentEvent;
import com.rohit.razorpay.common.enums.PaymentStatus;
import com.rohit.razorpay.common.exceptions.InvalidStateTransitionException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentStateMachine {

    private record Transition(PaymentStatus from, PaymentEvent event){};

    private final Map<Transition,PaymentStatus> stateTransitionMap =  Map.ofEntries(
            //created state transitions
            Map.entry(new Transition(PaymentStatus.CREATED,PaymentEvent.AUTHORIZE_ATTEMPT),PaymentStatus.AUTHORIZING),

            //authorizing state transitions
            Map.entry(new Transition(PaymentStatus.AUTHORIZING,PaymentEvent.AUTHORIZE_SUCCESS),PaymentStatus.AUTHORIZED),
            Map.entry(new Transition(PaymentStatus.AUTHORIZING,PaymentEvent.AUTHORIZE_FAIL),PaymentStatus.FAILED),

            //capture state transitions
            Map.entry(new Transition(PaymentStatus.AUTHORIZED,PaymentEvent.CAPTURE_REQUEST),PaymentStatus.CAPTURING),
            Map.entry(new Transition(PaymentStatus.CAPTURING,PaymentEvent.CAPTURE_SUCCESS),PaymentStatus.CAPTURED),
            Map.entry(new Transition(PaymentStatus.CAPTURING,PaymentEvent.CAPTURE_FAIL),PaymentStatus.AUTHORIZED),

            //settlement state transition
            Map.entry(new Transition(PaymentStatus.CAPTURED,PaymentEvent.SETTLE),PaymentStatus.SETTLED),

            //refund transitions
            Map.entry(new Transition(PaymentStatus.CAPTURED,PaymentEvent.REFUND_INIT),PaymentStatus.PARTIALLY_REFUNDED),
            Map.entry(new Transition(PaymentStatus.SETTLED,PaymentEvent.REFUND_INIT),PaymentStatus.PARTIALLY_REFUNDED),
            Map.entry(new Transition(PaymentStatus.CAPTURED,PaymentEvent.REFUND_COMPLETE),PaymentStatus.REFUNDED),
            Map.entry(new Transition(PaymentStatus.PARTIALLY_REFUNDED,PaymentEvent.REFUND_COMPLETE),PaymentStatus.REFUNDED),

            //cancellation state transitions
            Map.entry(new Transition(PaymentStatus.CREATED,PaymentEvent.CANCEL),PaymentStatus.CANCELLED),
            Map.entry(new Transition(PaymentStatus.AUTHORIZING,PaymentEvent.CANCEL),PaymentStatus.CANCELLED),
            Map.entry(new Transition(PaymentStatus.AUTHORIZED,PaymentEvent.CAPTURE_TIMEOUT),PaymentStatus.AUTH_EXPIRED)
    );
    public PaymentStatus transition(PaymentStatus currentStatus, PaymentEvent currentEvent){
        PaymentStatus next =  stateTransitionMap.get(new Transition(currentStatus,currentEvent));
        if(next == null){
            throw new InvalidStateTransitionException(currentStatus,currentEvent);
        }
        return next;
    }
}
