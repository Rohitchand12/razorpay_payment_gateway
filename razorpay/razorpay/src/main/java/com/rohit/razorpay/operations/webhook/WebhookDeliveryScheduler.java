package com.rohit.razorpay.operations.webhook;

import com.rohit.razorpay.common.enums.WebhookEventStatus;
import com.rohit.razorpay.operations.entity.WebhookEventEntity;
import com.rohit.razorpay.operations.repository.WebhookEventRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookDeliveryScheduler {
    private final WebhookRetryQueue retryQueue;
    private final WebhookEventRepository webhookEventRepository;
    private final WebhookDeliveryExecutor deliveryExecutor;
    private ExecutorService virtualThreadExecutor;

    @Value("${app.webhook.delivery.poll-batch-size:100}")
    private int batchSize;


    //there is a thread optimization here, will understand after studying about threads
    @PostConstruct
    void init(){
        virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
    }
    @PreDestroy
    void shutdown(){
        virtualThreadExecutor.shutdown();
    }

    @Scheduled(fixedDelay = 1000)
    public void pollAndDeliver(){
        Set<UUID> due = retryQueue.pollDue(batchSize);
        if(due.isEmpty()) return;
        for(UUID webhookEventId : due){
            virtualThreadExecutor.submit(()->{
                deliveryExecutor.deliver(webhookEventId);
            });
        }
    }

    //If for some reason the event is not enqueued to redis by consumer, this scheduler will enqueue the event pending in DB
    @Scheduled(fixedDelay = 10000)
    public void reconcileFromDatabase(){
        LocalDateTime now = LocalDateTime.now();
        List<WebhookEventEntity> due = webhookEventRepository.findByStatusAndNextRetryAtBefore(
                WebhookEventStatus.PENDING,now);
        for(WebhookEventEntity event : due){
            retryQueue.enqueueIfAbsent(event.getId(), event.getNextRetryAt());
        }
    }
}
