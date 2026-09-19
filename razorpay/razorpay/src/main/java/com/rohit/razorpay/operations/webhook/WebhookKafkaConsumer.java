package com.rohit.razorpay.operations.webhook;

import com.rohit.razorpay.common.dto.WebhookTargetDto;
import com.rohit.razorpay.common.enums.WebhookEventStatus;
import com.rohit.razorpay.common.utils.SignerUtil;
import com.rohit.razorpay.merchant.api.MerchantWebhookApi;
import com.rohit.razorpay.operations.entity.WebhookEventEntity;
import com.rohit.razorpay.operations.repository.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.CannotCreateTransactionException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookKafkaConsumer {

    private final MerchantWebhookApi merchantWebhookApi;
    private final ObjectMapper objectMapper;
    private final SignerUtil signerUtil;
    private final WebhookEventRepository webhookEventRepository;
    private final WebhookRetryQueue webhookRetryQueue;
    private final WebhookDlqRecorder dlqRecorder;

    @KafkaListener(topics = {
            "${app.kafka.topics.payment:payment.event}",
            "${app.kafka.topics.order:order.event}",
            "${app.kafka.topics.refund:refund.event}",
            "${app.kafka.topics.settlement:settlement.event}"
    })
    public void onWebhookEvent(ConsumerRecord<String, Map<String,Object>> record, Acknowledgment ack){
        try{
            //get the envelope from record and extract necessary things
            Map<String,Object> envelope = record.value();
            Map<String,Object> data = (Map<String,Object>) envelope.get("data");
            String eventType = (String) envelope.get("eventType");
            String rawMerchantId = (String) data.get("merchantId");

            if(rawMerchantId == null){
                log.warn("No merchant id found. Skipping event {}", eventType);
                ack.acknowledge();
                return;
            }
            UUID merchantId = UUID.fromString(rawMerchantId);

            //A merchant can have many webhook configs subscribed to same event type.
            List<WebhookTargetDto> targets = merchantWebhookApi.getActiveConfigsForEvent(merchantId,eventType);
            if(targets.isEmpty()){
                log.debug("No webhook target was found, skipping the event {}", eventType);
                ack.acknowledge();
                return;
            }

            //The signature data is the data that we will sign and store in db
            Map<String,Object> signatureData = Map.of("event", eventType, "payload", data);
            String signatureJson = objectMapper.writeValueAsString(signatureData);

            //For all targets sign the signature data and store the webhook event in db
            for(WebhookTargetDto target : targets){
                String signature = signerUtil.sign(signatureJson,target.webhookSecret());
                //store inside webhook event table
                WebhookEventEntity webhookEvent = WebhookEventEntity.builder()
                        .merchantId(merchantId)
                        .eventType(eventType)
                        .payload(data)
                        .targetUrl(target.targetUrl())
                        .signature(signature)
                        .status(WebhookEventStatus.PENDING)
                        .nextRetryAt(LocalDateTime.now())
                        .build();
                webhookEvent = webhookEventRepository.save(webhookEvent);
                //need to enqueue the event id to redis queue.
                webhookRetryQueue.enqueue(webhookEvent.getId(),webhookEvent.getNextRetryAt());
                log.info("Webhook event saved and enqueued to redis : {}", webhookEvent.getId());
            }
            ack.acknowledge();
        }catch (DataAccessException | CannotCreateTransactionException dbDown){
            log.error("Consumer failed to process the record due to db down, offset : {}",record.offset());
        }catch(Exception logicError){
            dlqRecorder.recordConsumerFailed(record,logicError.getMessage());
            ack.acknowledge();
        }
    }

}
