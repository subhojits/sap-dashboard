package com.example.sapdashboard.controller;


import com.example.sapdashboard.kafka.KafkaProducer;
import com.example.sapdashboard.model.IntegrationEvent;
import com.example.sapdashboard.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// @Controller tells Spring this handles web requests and returns HTML
@Controller
// Inject dependencies automatically
@RequiredArgsConstructor
// Enable logging
@Slf4j
public class DashboardController {

    // Inject the EventService
    private final EventService eventService;

    // Inject the KafkaProducer
    private final KafkaProducer kafkaProducer;

    // ===== ROUTE 1: SHOW DASHBOARD =====
    // GET http://localhost:8080/
    @GetMapping("/")
    public String dashboard(Model model) {
        // Get recent events from database
        List<IntegrationEvent> events = eventService.getRecentEvents();

        // Get statistics (total, success rate, etc.)
        Map<String, Object> stats = eventService.getDashboardStats();

        // Pass data to Thymeleaf template
        // In HTML, you'll access these as ${events} and ${stats}
        model.addAttribute("events", events);
        model.addAttribute("stats", stats);

        log.info("Dashboard loaded with {} events", events.size());

        // Return the HTML file name (from src/main/resources/templates/)
        return "dashboard";
    }

    // ===== ROUTE 2: REST API - RECEIVE NEW EVENT =====
    // POST http://localhost:8080/api/events
    // Body: {"orderId":"PO-123", "status":"SUCCESS", ...}
    @PostMapping("/api/events")
    public ResponseEntity<?> receiveEvent(@RequestBody IntegrationEvent event) {
        log.info("Received event via REST: {}", event.getOrderId());

        // Send the event to Kafka
        kafkaProducer.sendEvent(event);

        // Return success response
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Event received and published to Kafka",
                "orderId", event.getOrderId()
        ));
    }

    // ===== ROUTE 3: SEARCH BY ORDER ID =====
    // POST http://localhost:8080/search?orderId=PO-123
    @PostMapping("/search")
    public String search(@RequestParam String orderId, Model model) {
        // Search database for this order ID
        List<IntegrationEvent> results = eventService.searchByOrderId(orderId);

        // Add to model
        model.addAttribute("events", results);
        model.addAttribute("stats", eventService.getDashboardStats());
        model.addAttribute("searchQuery", orderId);

        log.info("Search performed for: {}", orderId);

        // Return same dashboard template but with filtered data
        return "dashboard";
    }

    // ===== ROUTE 4: FILTER BY STATUS =====
    // POST http://localhost:8080/filter?status=FAILED
    @PostMapping("/filter")
    public String filter(@RequestParam String status, Model model) {
        // Filter database by status
        List<IntegrationEvent> results = eventService.filterByStatus(status);

        // Add to model
        model.addAttribute("events", results);
        model.addAttribute("stats", eventService.getDashboardStats());
        model.addAttribute("filterStatus", status);

        log.info("Filter applied: {}", status);

        // Return same dashboard template but with filtered data
        return "dashboard";
    }

    // ===== ROUTE 5: REPROCESS FAILED EVENT =====
    // POST http://localhost:8080/reprocess/1
    @PostMapping("/reprocess/{id}")
    public String reprocessEvent(@PathVariable Long id) {
        // Mark event as PENDING so it can be retried
        eventService.reprocessEvent(id);

        log.info("Event {} marked for reprocessing", id);

        // Redirect back to dashboard
        return "redirect:/";
    }

    // ===== ROUTE 6: HEALTH CHECK =====
    // GET http://localhost:8080/health
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        // Return JSON with status
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "message", "Dashboard is running",
                "timestamp", System.currentTimeMillis()
        ));
    }
}
