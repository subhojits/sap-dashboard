package com.example.sapdashboard.util;

import com.example.sapdashboard.model.IntegrationEvent;
import com.example.sapdashboard.repository.IntegrationEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class SampleDataGenerator {

    private final IntegrationEventRepository repository;
    private static final Random random = new Random();

    private static final String[] STATUSES = {"SUCCESS", "FAILED", "PENDING"};
    private static final String[] INTEGRATIONS = {
            "Order-to-SAP",
            "Customer-Sync",
            "Inventory-Update",
            "Invoice-Processing"
    };
    private static final String[] MESSAGES = {
            "Order placed successfully",
            "Customer record created",
            "Stock quantity updated",
            "Invoice sent to customer",
            "Payment processed",
            "Shipment confirmed",
            "Data synchronized",
            "Transaction completed"
    };
    private static final String[] ERROR_MESSAGES = {
            "Connection timeout to SAP",
            "Invalid data format",
            "Authentication failed",
            "Network error",
            "Service unavailable",
            "Data validation error",
            "Permission denied",
            "Resource not found"
    };

    @EventListener(ApplicationReadyEvent.class)
    public void generateSampleData() {
        System.out.println("🔄 Generating sample data...");

        for (int i = 1; i <= 20; i++) {
            IntegrationEvent event = new IntegrationEvent();
            event.setOrderId("PO-" + String.format("%05d", i));
            event.setStatus(STATUSES[random.nextInt(STATUSES.length)]);
            event.setIntegrationName(INTEGRATIONS[random.nextInt(INTEGRATIONS.length)]);
            event.setMessage(MESSAGES[random.nextInt(MESSAGES.length)]);

            int minutesAgo = random.nextInt(60) + 1;
            event.setTimestamp(LocalDateTime.now().minusMinutes(minutesAgo));

            if (event.getStatus().equals("FAILED")) {
                event.setErrorDetails(ERROR_MESSAGES[random.nextInt(ERROR_MESSAGES.length)]);
            } else {
                event.setErrorDetails("");
            }

            repository.save(event);
            System.out.println("✓ Created: " + event.getOrderId() + " - " + event.getStatus());
        }

        System.out.println("\n✅ Sample data generated successfully!");
        System.out.println("📊 Total events created: 20");
        System.out.println("🌐 Open browser: http://localhost:8080\n");
    }
}
