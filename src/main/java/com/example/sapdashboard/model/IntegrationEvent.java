package com.example.sapdashboard.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "integration_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(length = 500)
    private String message;

    @Column(length = 1000)
    private String errorDetails;

    @Column(nullable = false)
    private String integrationName;

    public IntegrationEvent(String orderId, String status, String message, String integrationName) {
        this.orderId = orderId;
        this.status = status;
        this.message = message;
        this.integrationName = integrationName;
        this.timestamp = LocalDateTime.now();
        this.errorDetails = "";
    }

    public boolean isPassed() {
        return "SUCCESS".equalsIgnoreCase(this.status);
    }

    public String getTimeAgo() {
        LocalDateTime now = LocalDateTime.now();
        long seconds = java.time.temporal.ChronoUnit.SECONDS.between(this.timestamp, now);

        if (seconds < 60) return seconds + "s ago";
        long minutes = seconds / 60;
        if (minutes < 60) return minutes + "m ago";
        long hours = minutes / 60;
        if (hours < 24) return hours + "h ago";
        long days = hours / 24;
        return days + "d ago";
    }
}
