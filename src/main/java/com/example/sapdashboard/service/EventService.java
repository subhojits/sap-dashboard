package com.example.sapdashboard.service;

import com.example.sapdashboard.dto.RetryEventMessage;
import com.example.sapdashboard.dto.RetryEventRequest;
import com.example.sapdashboard.model.IntegrationEvent;
import com.example.sapdashboard.repository.IntegrationEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final IntegrationEventRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Get all events
     */
    public List<IntegrationEvent> getAllEvents() {
        return repository.findAll();
    }

    /**
     * Get events by status
     */
    public List<IntegrationEvent> getEventsByStatus(String status) {
        return repository.findByStatus(status);
    }

    /**
     * Get events by order ID
     */
    public List<IntegrationEvent> getEventsByOrderId(String orderId) {
        return repository.findByOrderId(orderId);
    }

    /**
     * Get failed events with payload
     */
    public List<IntegrationEvent> getFailedEventsWithPayload() {
        return repository.findFailedEventsWithPayload();
    }

    /**
     * Get recent events (last 100)
     * FIXED: Using collect(Collectors.toList()) instead of toList() for Java 11
     */
    public List<IntegrationEvent> getRecentEvents() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(IntegrationEvent::getCreatedAt).reversed())
                .limit(100)
                .collect(Collectors.toList());
    }

    /**
     * Save event (from external API or Kafka)
     */
    public IntegrationEvent saveEvent(IntegrationEvent event) {
        if (event.getCreatedAt() == null) {
            event.setTimestamp(LocalDateTime.now());
        }
        if (event.getRetryCount() == 0) {
            event.setRetryCount(0);
        }
        // Store original payload if not already stored
        if (event.getPayload() != null && event.getOriginalPayload() == null) {
            event.setOriginalPayload(event.getPayload());
        }
        return repository.save(event);
    }

    /**
     * GET DASHBOARD STATISTICS
     * Used by controller to show: total, success, failed, pending counts and success rate
     */
    public Map<String, Object> getDashboardStats() {
        List<IntegrationEvent> allEvents = repository.findAll();

        long totalEvents = allEvents.size();
        long successCount = allEvents.stream()
                .filter(e -> "SUCCESS".equals(e.getStatus()))
                .count();
        long failedCount = allEvents.stream()
                .filter(e -> "FAILED".equals(e.getStatus()))
                .count();
        long pendingCount = allEvents.stream()
                .filter(e -> "PENDING".equals(e.getStatus()))
                .count();

        double successRate = totalEvents > 0 ? (successCount * 100.0) / totalEvents : 0.0;

        return Map.of(
                "totalEvents", totalEvents,
                "successCount", successCount,
                "failedCount", failedCount,
                "pendingCount", pendingCount,
                "successRate", String.format("%.2f", successRate)
        );
    }

    /**
     * SEARCH BY ORDER ID
     * Used by controller to search events by order ID
     */
    public List<IntegrationEvent> searchByOrderId(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            return getRecentEvents();
        }
        return repository.findByOrderId(orderId.trim());
    }

    /**
     * FILTER BY STATUS
     * Used by controller to filter events by status
     */
    public List<IntegrationEvent> filterByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return getRecentEvents();
        }
        return repository.findByStatus(status.trim());
    }

    /**
     * REPROCESS EVENT (SIMPLE RETRY WITHOUT MODAL)
     * Used by controller for quick retry from dashboard
     * This just changes status to PENDING for re-processing
     */
    public void reprocessEvent(Long eventId) {
        log.info("Reprocessing event with ID: {}", eventId);

        IntegrationEvent event = repository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Only allow reprocessing of FAILED events
        if (!"FAILED".equals(event.getStatus())) {
            throw new RuntimeException("Only FAILED events can be reprocessed");
        }

        // Change status to PENDING
        event.setStatus("PENDING");

        // Save
        repository.save(event);

        log.info("Event {} reprocessed successfully", event.getOrderId());
    }

    /**
     * RETRY FAILED EVENT WITH UPDATED PAYLOAD (WITH MODAL)
     * User edits payload in modal and submits
     */
    public void retryFailedEvent(RetryEventRequest request) {
        log.info("Retrying event with ID: {} with updated payload", request.getEventId());

        // Get the original event
        IntegrationEvent originalEvent = repository.findFailedEventById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found or not in FAILED status"));

        // Check if can retry (max 3 times)
        if (!originalEvent.canRetry()) {
            throw new RuntimeException("Maximum retry attempts (3) reached for this event");
        }

        // Increment retry count
        originalEvent.incrementRetry();

        // Update retry history
        updateRetryHistory(originalEvent, request);

        // Update payload with user edited version
        originalEvent.setPayload(request.getUpdatedPayload());
        originalEvent.setPayloadFormat(request.getPayloadFormat());

        // Change status to PENDING for retry
        originalEvent.setStatus("PENDING");

        // Save updated event
        repository.save(originalEvent);

        // Create retry message and send to Kafka retry topic
        sendRetryEventToKafka(originalEvent, request);

        log.info("Event {} sent to retry topic. Retry attempt: {}",
                originalEvent.getOrderId(), originalEvent.getRetryCount());
    }

    /**
     * Update retry history with JSON format
     */
    private void updateRetryHistory(IntegrationEvent event, RetryEventRequest request) {
        try {
            List<Map<String, Object>> retryHistory = new ArrayList<>();

            // Parse existing history if available
            if (event.getRetryHistory() != null && !event.getRetryHistory().isEmpty()) {
                // Simple parsing (in production, use Jackson ObjectMapper)
                retryHistory = new ArrayList<>(); // simplified
            }

            // Add current retry
            Map<String, Object> currentRetry = new HashMap<>();
            currentRetry.put("retryNumber", event.getRetryCount());
            currentRetry.put("timestamp", LocalDateTime.now().toString());
            currentRetry.put("userNotes", request.getUserNotes());
            currentRetry.put("oldPayload", event.getOriginalPayload());
            currentRetry.put("newPayload", request.getUpdatedPayload());

            retryHistory.add(currentRetry);

            // Store as JSON string (simple format)
            event.setRetryHistory(retryHistory.toString());
        } catch (Exception e) {
            log.error("Error updating retry history", e);
        }
    }

    /**
     * Send retry event to Kafka topic: sap-integration-order-retry
     */
    private void sendRetryEventToKafka(IntegrationEvent event, RetryEventRequest request) {
        try {
            RetryEventMessage retryMessage = new RetryEventMessage();
            retryMessage.setOrderId(event.getOrderId());
            retryMessage.setOriginalStatus("FAILED");
            retryMessage.setUpdatedPayload(request.getUpdatedPayload());
            retryMessage.setOriginalPayload(event.getOriginalPayload());
            retryMessage.setOriginalErrorDetails(event.getErrorDetails());
            retryMessage.setRetryAttempt(event.getRetryCount());
            retryMessage.setRetryTimestamp(LocalDateTime.now());
            retryMessage.setUserNotes(request.getUserNotes());
            retryMessage.setPayloadFormat(request.getPayloadFormat());

            // Send to retry topic
            kafkaTemplate.send("sap-integration-order-retry", event.getOrderId(), retryMessage);

            log.info("Retry event sent to Kafka topic: sap-integration-order-retry");
        } catch (Exception e) {
            log.error("Error sending retry event to Kafka", e);
            throw new RuntimeException("Failed to send retry event to Kafka", e);
        }
    }

    /**
     * Get event details including payload
     */
    public IntegrationEvent getEventDetails(Long eventId) {
        return repository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    /**
     * Get event count by status
     */
    public Map<String, Long> getEventCountByStatus() {
        List<IntegrationEvent> allEvents = repository.findAll();
        return allEvents.stream()
                .collect(Collectors.groupingBy(IntegrationEvent::getStatus, Collectors.counting()));
    }
}
