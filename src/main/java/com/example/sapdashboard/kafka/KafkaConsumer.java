package com.example.sapdashboard.kafka;


import com.example.sapdashboard.model.IntegrationEvent;
import com.example.sapdashboard.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

// @Service marks this as a service
@Service
// Inject dependencies automatically
@RequiredArgsConstructor
// Enable logging
@Slf4j
public class KafkaConsumer {

    // Inject the EventService to save events
    private final EventService eventService;

    // ===== LISTEN FOR EVENTS FROM KAFKA =====
    // @KafkaListener tells Spring to listen to a specific topic
    @KafkaListener(
            // Listen to this topic
            topics = "sap-integration-events",
            // Consumer group ID (important for Kafka)
            groupId = "dashboard-group"
    )
    // This method is called every time a new message arrives
    public void consume(IntegrationEvent event) {
        log.info("Received event from Kafka: {}", event.getOrderId());

        // Save the event to the database
        eventService.saveEvent(event);
    }
}
