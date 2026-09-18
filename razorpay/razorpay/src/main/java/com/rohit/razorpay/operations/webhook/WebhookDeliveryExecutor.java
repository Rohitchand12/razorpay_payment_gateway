package com.rohit.razorpay.operations.webhook;

import com.rohit.razorpay.common.enums.WebhookEventStatus;
import com.rohit.razorpay.operations.entity.WebhookEventEntity;
import com.rohit.razorpay.operations.repository.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookDeliveryExecutor {

    private final RestClient restClient;
    private final WebhookEventRepository webhookEventRepository;
    private final WebhookRetryQueue retryQueue;
    private final WebhookDlqRecorder webhookDlqRecorder;


    @Value("${app.webhook.delivery.signature-header:X-Razorpay-Signature}")
    private String signatureHeader;

    private static final int MAX_ATTEMPTS = 7;

    private final List<Duration> BACKOFF = List.of(
            Duration.ofMinutes(1),
            Duration.ofMinutes(5),
            Duration.ofMinutes(30),
            Duration.ofHours(2),
            Duration.ofHours(8),
            Duration.ofHours(24)
            );

    void deliver(UUID eventId){
        Optional<WebhookEventEntity> webhookEvent = webhookEventRepository.findById(eventId);
        if(webhookEvent.isEmpty()){
            log.warn("No webhook event found for this id: {}",eventId);
            return;
        }
        WebhookEventEntity event = webhookEvent.get();
        if(event.getStatus() == WebhookEventStatus.DEAD || event.getStatus() == WebhookEventStatus.DELIVERED){
            log.warn("Cannot deliver the event {} with status {}", eventId,event.getStatus());
            return;
        }
        event.setAttempts(event.getAttempts()+1);
        event.setLastAttemptedAt(LocalDateTime.now());

        try{
            var response = restClient.post()
                    .uri(event.getTargetUrl())
                    .header(signatureHeader, event.getSignature())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "event", event.getEventType(),
                            "payload", event.getPayload()
                    ))
                    .retrieve()
                    .toBodilessEntity();
            int statusCode = response.getStatusCode().value();
            event.setLastResponseCode(statusCode);
            if(response.getStatusCode().is2xxSuccessful()){
                event.setStatus(WebhookEventStatus.DELIVERED);
                event.setDeliveredAt(LocalDateTime.now());
                webhookEventRepository.save(event);
                return;
            }
            handleAttemptFailed(event, "HTTP"+statusCode);
        }catch(RestClientException e){
            event.setLastResponseBody(e.getMessage());
            handleAttemptFailed(event, e.getMessage());
        }

    }

    private void handleAttemptFailed(WebhookEventEntity event, String error) {
        event.setLastResponseBody(error);
        if(event.getAttempts() >= MAX_ATTEMPTS){
            event.setStatus(WebhookEventStatus.DEAD);
            //DLQ recording here
            webhookDlqRecorder.recordAfterAttemptsExhausted(event,error);
            return;
        }
        //backoff retry
        Duration backoff = BACKOFF.get(event.getAttempts()-1);
        LocalDateTime nextRetryAt = LocalDateTime.now().plus(backoff);
        event.setStatus(WebhookEventStatus.FAILED);
        event.setNextRetryAt(nextRetryAt);
        webhookEventRepository.save(event);
        retryQueue.enqueue(event.getId(),nextRetryAt);
    }

}
