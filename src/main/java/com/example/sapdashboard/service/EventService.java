package com.example.sapdashboard.service;


import com.example.sapdashboard.model.IntegrationEvent;
import com.example.sapdashboard.repository.IntegrationEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// @Service marks this as a service class
@Service
// @RequiredArgsConstructor injects dependencies automatically (constructor injection)
@RequiredArgsConstructor
// @Slf4j gives us a logger called "log" for free
@Slf4j
public class EventService {

    // The repository to access database
    // Spring automatically injects this (no need to create it manually)
    private final IntegrationEventRepository repository;

    // ===== GET RECENT EVENTS =====
    // Returns the 50 most recent events
    public List<IntegrationEvent> getRecentEvents() {
        return repository.findTop50ByOrderByTimestampDesc();
    }

    // ===== SEARCH BY ORDER ID =====
    // Search events containing a specific order ID
    public List<IntegrationEvent> searchByOrderId(String orderId) {
        return repository.findByOrderIdContainingIgnoreCaseOrderByTimestampDesc(orderId);
    }

    // ===== FILTER BY STATUS =====
    // Get all events with a specific status
    public List<IntegrationEvent> filterByStatus(String status) {
        return repository.findByStatusOrderByTimestampDesc(status);
    }

    // ===== SAVE NEW EVENT =====
    // Save an event to the database
    public IntegrationEvent saveEvent(IntegrationEvent event) {
        log.info("Saving event: {}", event.getOrderId());
        return repository.save(event);
    }

    // ===== GET DASHBOARD STATISTICS =====
    // Calculate all statistics for the dashboard
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Count total events
        Long totalEvents = repository.count();

        // Count events by status
        Long successCount = repository.countByStatus("SUCCESS");
        Long failedCount = repository.countByStatus("FAILED");
        Long pendingCount = repository.countByStatus("PENDING");

        // Calculate success rate percentage
        // If totalEvents is 0, set to 0% (avoid division by zero)
        double successRate = totalEvents > 0 ? (successCount * 100.0) / totalEvents : 0;

        // Put all stats in the map
        stats.put("totalEvents", totalEvents);
        stats.put("successCount", successCount);
        stats.put("failedCount", failedCount);
        stats.put("pendingCount", pendingCount);
        // Format to 1 decimal place (e.g., "98.5%")
        stats.put("successRate", String.format("%.1f", successRate));

        return stats;
    }

    // ===== GET FAILED EVENTS =====
    // Get all failed events (for the retry feature)
    public List<IntegrationEvent> getFailedEvents() {
        return repository.findByStatusOrderByTimestampDesc("FAILED");
    }

    // ===== REPROCESS FAILED EVENT =====
    // Mark a failed event as PENDING so it can be retried
    public IntegrationEvent reprocessEvent(Long eventId) {
        return repository.findById(eventId).map(event -> {
            // Change status to PENDING
            event.setStatus("PENDING");
            // Clear error details
            event.setErrorDetails("");
            log.info("Reprocessing event: {}", event.getOrderId());
            // Save back to database
            return repository.save(event);
        }).orElse(null);
    }

    // ===== GET SUMMARY BY INTEGRATION =====
    // Count events per integration name
    public Map<String, Long> getSummaryByIntegration() {
        Map<String, Long> summary = new HashMap<>();
        List<IntegrationEvent> allEvents = repository.findAll();

        // Group events by integration name and count
        allEvents.stream()
                .collect(java.util.stream.Collectors.groupingByConcurrent(
                        IntegrationEvent::getIntegrationName,
                        java.util.stream.Collectors.counting()
                ))
                .forEach((integration, count) -> summary.put(integration, count));

        return summary;
    }
}
