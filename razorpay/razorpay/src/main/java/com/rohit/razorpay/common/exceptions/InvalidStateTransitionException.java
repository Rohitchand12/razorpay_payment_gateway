package com.rohit.razorpay.common.exceptions;

import com.rohit.razorpay.common.enums.PaymentEvent;
import com.rohit.razorpay.common.enums.PaymentStatus;
import lombok.Getter;

@Getter
public class InvalidStateTransitionException extends RuntimeException {
    private final PaymentStatus currentStatus;
    private final PaymentEvent currentEvent;

    public InvalidStateTransitionException(PaymentStatus currentStatus, PaymentEvent currentEvent) {
        super("Invalid transition from "+ currentStatus.toString() + " on event " + currentEvent.toString());
        this.currentEvent = currentEvent;
        this.currentStatus = currentStatus;
    }
}
