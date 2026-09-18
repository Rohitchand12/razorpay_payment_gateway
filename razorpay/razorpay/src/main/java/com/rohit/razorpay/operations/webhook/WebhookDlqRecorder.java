package com.rohit.razorpay.operations.webhook;

import com.rohit.razorpay.common.enums.WebhookEventStatus;
import com.rohit.razorpay.operations.entity.DlqEventEntity;
import com.rohit.razorpay.operations.entity.WebhookEventEntity;
import com.rohit.razorpay.operations.repository.DlqEventRepository;
import com.rohit.razorpay.operations.repository.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookDlqRecorder {
    private final WebhookEventRepository webhookEventRepository;
    private final DlqEventRepository dlqEventRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordAfterAttemptsExhausted(WebhookEventEntity event, String finalError){
        event.setStatus(WebhookEventStatus.DEAD);
        webhookEventRepository.save(event);

        DlqEventEntity dlqEvent = DlqEventEntity.builder()
                .webhookEvent(event)
                .finalError(finalError)
                .movedAt(LocalDateTime.now())
                .merchantId(event.getMerchantId())
                .payload(event.getPayload())
                .build();
        dlqEventRepository.save(dlqEvent);
    }

    public void recordConsumerFailed(ConsumerRecord<String, Map<String, Object>> record, String message) {
        Map<String,Object> envelope = record.value();

        UUID merchantId = null;
        try {
            Map<String, Object> data = (Map<String, Object>) envelope.get("data");
            String rawMerchantId = data != null ? (String) data.get("merchantId") : null;

            if (rawMerchantId != null) {
                merchantId = UUID.fromString(rawMerchantId);
            }
        }catch (Exception ignored){

        }
        DlqEventEntity dlqEvent = DlqEventEntity.builder()
                .webhookEvent(null)
                .finalError(message)
                .movedAt(LocalDateTime.now())
                .merchantId(merchantId)
                .payload(envelope != null ? envelope : Map.of())
                .build();
        dlqEventRepository.save(dlqEvent);
    }
}
