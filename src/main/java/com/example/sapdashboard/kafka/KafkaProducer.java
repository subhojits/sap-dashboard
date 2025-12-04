package com.example.sapdashboard.kafka;

import com.example.sapdashboard.model.IntegrationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Send event to Kafka topic
     */
    public void sendEvent(String topic, String key, IntegrationEvent event) {
        log.info("Sending event to topic: {}", topic);
        kafkaTemplate.send(topic, key, event);
    }

    /**
     * Send to main events topic
     */
    public void sendToEventsTopic(IntegrationEvent event) {
        sendEvent("sap-integration-events", event.getOrderId(), event);
    }

    /**
     * Send to retry topic
     */
    public void sendToRetryTopic(String key, Object message) {
        kafkaTemplate.send("sap-integration-order-retry", key, message);
    }
}
