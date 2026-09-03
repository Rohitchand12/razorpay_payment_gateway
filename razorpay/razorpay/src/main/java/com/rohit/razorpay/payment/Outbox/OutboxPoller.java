package com.rohit.razorpay.payment.Outbox;

import com.rohit.razorpay.common.config.KafkaProperties;
import com.rohit.razorpay.common.enums.OutboxStatus;
import com.rohit.razorpay.payment.entity.Outbox;
import com.rohit.razorpay.payment.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxPoller {

    private final OutboxRepository outboxRepository;
    private final KafkaProperties kafkaProperties;
    private final OutboxResultHandler outboxResultHandler;

    private final KafkaTemplate<String,Object> kafka;

    @Scheduled(fixedDelay = 5000)
    public void poller(){
        List<Outbox> pendingEvents = outboxRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        for(Outbox event: pendingEvents){
            try{
                String key = extractMerchantId(event.getPayload());
                String topic = kafkaProperties.topicFor(event.getAggregateType());

                Map<String,Object> envelope = Map.of(
                        "eventType",event.getEventType(),
                        "aggregateType", event.getAggregateType(),
                        "aggregateId", event.getAggregateId(),
                        "data", event.getPayload()
                );

                kafka.send(topic,key,envelope).get(5, TimeUnit.SECONDS);
                outboxResultHandler.handleEventPublished(event);
            }catch(Exception e){
                log.error("Outbox event failed. Event id : {}, attempts: {}", event.getId(), event.getAttempts());
                outboxResultHandler.handleEventFailed(event,e.getMessage());
            }
        }
    }

    private String extractMerchantId(Map<String,Object> payload) {
        Object merchantId = payload.get("merchantId");
        return merchantId != null ? merchantId.toString() : "unknown";
    }

}
