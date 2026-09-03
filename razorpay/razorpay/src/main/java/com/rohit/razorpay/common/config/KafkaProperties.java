package com.rohit.razorpay.common.config;

import com.rohit.razorpay.common.enums.AggregateType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "app.kafka")
@Getter
@Setter
public class KafkaProperties {

    Map<String,String> topics = new HashMap<>();
    public String topicFor(AggregateType aggregateType){
        String topic = topics.get(aggregateType.name().toLowerCase());
        if(topic == null){
            throw new IllegalArgumentException("No topic available for provided aggregate type");
        }
        return topic;
    }

}
