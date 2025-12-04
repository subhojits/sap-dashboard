package com.example.sapdashboard.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.Duration;

@Entity
@Table(name = "integration_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "payload", columnDefinition = "LONGTEXT")
    private String payload;

    @Column(name = "original_payload", columnDefinition = "LONGTEXT")
    private String originalPayload;

    @Column(name = "payload_format")
    private String payloadFormat;

    @Column(name = "retry_count")
    private int retryCount = 0;

    @Column(name = "retry_history", columnDefinition = "TEXT")
    private String retryHistory;

    @Column(name = "error_details", columnDefinition = "TEXT")
    private String errorDetails;

    // 🚨 NEW FIELD ADDED HERE 🚨
    @Column(name = "integration_name")
    private String integrationName;
    // 🚨 END NEW FIELD 🚨

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // --- METHODS ---

    public LocalDateTime getTimestamp() {
        return createdAt;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.createdAt = timestamp;
    }

    public boolean canRetry() {
        return retryCount < 3; // Max 3 retries
    }

    public void incrementRetry() {
        this.retryCount++;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Calculate time elapsed since event creation (e.g., "5 minutes ago")
     */
    public String getTimeAgo() {
        if (this.createdAt == null) {
            return "Unknown";
        }

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(this.createdAt, now);

        long seconds = duration.getSeconds();

        if (seconds < 60) {
            return seconds + "s ago";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes + "m ago";
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            return hours + "h ago";
        } else {
            long days = seconds / 86400;
            return days + "d ago";
        }
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
