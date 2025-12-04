package com.example.sapdashboard.controller;

import com.example.sapdashboard.dto.RetryEventRequest;
import com.example.sapdashboard.model.IntegrationEvent;
import com.example.sapdashboard.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final EventService eventService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // ===== WEB PAGES (Thymeleaf) =====

    /**
     * GET / - Show Dashboard
     * Main dashboard view with all events and statistics
     */
    @GetMapping("/")
    public String dashboard(Model model) {
        log.info("Loading dashboard");

        // Get recent events
        List<IntegrationEvent> events = eventService.getRecentEvents();

        // Get statistics
        Map<String, Object> stats = eventService.getDashboardStats();

        // Pass data to Thymeleaf template
        model.addAttribute("events", events);
        model.addAttribute("stats", stats);

        return "dashboard";
    }

    /**
     * GET /search - Search events by Order ID
     * Used for search functionality
     */
    @GetMapping("/search")
    public String search(@RequestParam(value = "orderId", required = false) String orderId, Model model) {
        log.info("Searching events with orderId: {}", orderId);

        List<IntegrationEvent> events = eventService.searchByOrderId(orderId);
        Map<String, Object> stats = eventService.getDashboardStats();

        model.addAttribute("events", events);
        model.addAttribute("stats", stats);
        model.addAttribute("searchQuery", orderId);

        return "dashboard";
    }

    /**
     * GET /filter - Filter events by status
     * Used for status filtering
     */
    @GetMapping("/filter")
    public String filter(@RequestParam(value = "status", required = false) String status, Model model) {
        log.info("Filtering events with status: {}", status);

        List<IntegrationEvent> events = eventService.filterByStatus(status);
        Map<String, Object> stats = eventService.getDashboardStats();

        model.addAttribute("events", events);
        model.addAttribute("stats", stats);
        model.addAttribute("filterStatus", status);

        return "dashboard";
    }

    // ===== REST API ENDPOINTS =====

    /**
     * POST /api/events - Create/Save new event
     * Receive event from external system and save to database
     */
    @PostMapping("/api/events")
    public ResponseEntity<IntegrationEvent> createEvent(@RequestBody IntegrationEvent event) {
        log.info("Creating new event for order: {}", event.getOrderId());

        IntegrationEvent savedEvent = eventService.saveEvent(event);

        // Also publish to Kafka topic
        try {
            kafkaTemplate.send("sap-integration-events", event.getOrderId(), savedEvent);
        } catch (Exception e) {
            log.error("Error publishing to Kafka", e);
        }

        return ResponseEntity.ok(savedEvent);
    }

    /**
     * GET /api/events - Get all events (REST API)
     */
    @GetMapping("/api/events")
    public ResponseEntity<List<IntegrationEvent>> getAllEvents() {
        List<IntegrationEvent> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    /**
     * GET /api/events/{id} - Get event details by ID
     * Used by modal to fetch event details including payload
     */
    @GetMapping("/api/events/{id}")
    public ResponseEntity<IntegrationEvent> getEventDetails(@PathVariable Long id) {
        log.info("Fetching event details for ID: {}", id);

        IntegrationEvent event = eventService.getEventDetails(id);
        return ResponseEntity.ok(event);
    }

    /**
     * GET /api/events/status/{status} - Get events by status
     */
    @GetMapping("/api/events/status/{status}")
    public ResponseEntity<List<IntegrationEvent>> getEventsByStatus(@PathVariable String status) {
        List<IntegrationEvent> events = eventService.getEventsByStatus(status);
        return ResponseEntity.ok(events);
    }

    /**
     * GET /api/events/search/{orderId} - Search events by Order ID
     */
    @GetMapping("/api/events/search/{orderId}")
    public ResponseEntity<List<IntegrationEvent>> searchByOrderId(@PathVariable String orderId) {
        List<IntegrationEvent> events = eventService.searchByOrderId(orderId);
        return ResponseEntity.ok(events);
    }

    /**
     * GET /api/stats - Get dashboard statistics
     */
    @GetMapping("/api/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = eventService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * POST /api/events/{id}/reprocess - Quick reprocess (changes status to PENDING)
     * Used by simple retry button in dashboard
     */
    @PostMapping("/api/events/{id}/reprocess")
    public ResponseEntity<Map<String, String>> reprocessEvent(@PathVariable Long id) {
        log.info("Reprocessing event: {}", id);

        try {
            eventService.reprocessEvent(id);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Event reprocessed successfully"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * POST /api/events/{id}/retry - Retry with updated payload (Modal)
     * Used by payload editor modal when user submits edited payload
     */
    @PostMapping("/api/events/{id}/retry")
    public ResponseEntity<Map<String, String>> retryFailedEvent(
            @PathVariable Long id,
            @RequestBody RetryEventRequest request) {

        log.info("Retrying event: {} with updated payload", id);

        try {
            request.setEventId(id);
            eventService.retryFailedEvent(request);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Event sent to retry topic successfully",
                    "eventId", id.toString()
            ));
        } catch (RuntimeException e) {
            log.error("Error retrying event", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * GET /health - Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "message", "SAP Dashboard Application is running",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    /**
     * GET /api/events/count/byStatus - Get event count by status
     */
    @GetMapping("/api/events/count/byStatus")
    public ResponseEntity<Map<String, Long>> getEventCountByStatus() {
        Map<String, Long> counts = eventService.getEventCountByStatus();
        return ResponseEntity.ok(counts);
    }
}
