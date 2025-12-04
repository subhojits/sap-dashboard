package com.example.sapdashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetryEventRequest {
    private Long eventId;
    private String updatedPayload; // User edited payload
    private String payloadFormat; // XML or JSON
    private String userNotes; // Optional notes
}
