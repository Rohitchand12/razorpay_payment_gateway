package com.rohit.razorpay.operations.webhook;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class WebhookRetryQueue {
    private final StringRedisTemplate redis;

    @Value("${app.webhook.delivery.redis-key:webhook-retry}")
    private String key;

    public void enqueue(UUID webhookEventId, LocalDateTime retryAt){
        long time = getEpochMilli(retryAt);
        redis.opsForZSet().add(key,webhookEventId.toString(),time);
    }

    public Set<UUID> pollDue(int limit){
        long now = getEpochMilli(LocalDateTime.now());
        Set<ZSetOperations.TypedTuple<String>> due = redis.opsForZSet()
                .rangeByScoreWithScores(key,0,now,0,limit);
        if(due == null || due.isEmpty()) {
           return Set.of();
        }
        due.forEach(tuple -> redis.opsForZSet().remove(key,tuple.getValue()));
        return due.stream()
                .map(tuple-> UUID.fromString(tuple.getValue()))
                .collect(Collectors.toSet());
    }

    public void enqueueIfAbsent(UUID id, LocalDateTime nextRetryAt) {
        redis.opsForZSet().addIfAbsent(key,id.toString(), getEpochMilli(nextRetryAt));
    }

    private static long getEpochMilli(LocalDateTime retryAt) {
        return retryAt.toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}
