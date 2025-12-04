package com.example.sapdashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetryEventMessage {
    private String orderId;
    private String originalStatus;
    private String updatedPayload;
    private String originalPayload;
    private String originalErrorDetails;
    private Integer retryAttempt;
    private LocalDateTime retryTimestamp;
    private String userNotes;
    private String payloadFormat;
}
