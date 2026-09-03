package com.rohit.razorpay.payment.Outbox;

import com.rohit.razorpay.common.enums.AggregateType;
import com.rohit.razorpay.payment.entity.Outbox;
import com.rohit.razorpay.payment.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {
    private final OutboxRepository outboxRepository;

    public void publish(AggregateType aggregateType, UUID aggregateId
            , String eventTye, Map<String,Object> payload){
        Outbox newOutboxEvent = Outbox.builder()
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .eventType(eventTye)
                .payload(payload)
                .build();
        outboxRepository.save(newOutboxEvent);
    }
}
