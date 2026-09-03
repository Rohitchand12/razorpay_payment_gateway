package com.rohit.razorpay.payment.Outbox;

import com.rohit.razorpay.common.enums.OutboxStatus;
import com.rohit.razorpay.payment.entity.Outbox;
import com.rohit.razorpay.payment.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OutboxResultHandler {

    private static final Integer MAX_ATTEMPTS = 3;
    private final OutboxRepository outboxRepository;

    @Transactional
    public void handleEventPublished(Outbox event){
        event.setStatus(OutboxStatus.PUBLISHED);
        event.setPublishedAt(LocalDateTime.now());
        outboxRepository.save(event);
    }

    @Transactional
    public void handleEventFailed(Outbox event, String errorMessage){
        event.setAttempts(event.getAttempts()+1);
        String lastError = errorMessage.length() > 1000 ? errorMessage.substring(0,1000): errorMessage;
        event.setLastError(lastError);
        if(event.getAttempts() >= MAX_ATTEMPTS){
            event.setStatus(OutboxStatus.FAILED);
        }
        outboxRepository.save(event);
    }

}
