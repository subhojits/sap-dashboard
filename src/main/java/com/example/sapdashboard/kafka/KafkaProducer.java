package com.example.sapdashboard.kafka;


import com.example.sapdashboard.model.IntegrationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

// @Service marks this as a service
@Service
// Inject dependencies automatically
@RequiredArgsConstructor
// Enable logging
@Slf4j
public class KafkaProducer {

    // KafkaTemplate is Spring's tool to send messages to Kafka
    // <String, IntegrationEvent> means: key is String, value is IntegrationEvent
    private final KafkaTemplate<String, IntegrationEvent> kafkaTemplate;

    // ===== SEND EVENT TO KAFKA =====
    // This method takes an event and publishes it to Kafka
    public void sendEvent(IntegrationEvent event) {
        log.info("Publishing event to Kafka: {}", event.getOrderId());

        // Send the event
        // "sap-integration-events" = the topic name
        // event.getOrderId() = the key (used for partitioning)
        // event = the actual message content
        kafkaTemplate.send("sap-integration-events", event.getOrderId(), event);
    }
}
