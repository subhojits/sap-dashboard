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
            "Shipment confirmed"
    };
    private static final String[] ERROR_MESSAGES = {
            "Connection timeout to SAP",
            "Invalid data format",
            "Authentication failed",
            "Network error",
            "Missing required field",
            "Data validation error"
    };

    // Sample XML payload (Java 11 compatible - using concatenation)
    private static final String SAMPLE_XML_PAYLOAD =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<SalesOrder>\n" +
                    "    <OrderID>{{ORDER_ID}}</OrderID>\n" +
                    "    <CustomerID>CUST-{{CUST_ID}}</CustomerID>\n" +
                    "    <OrderDate>2025-12-04</OrderDate>\n" +
                    "    <TotalAmount>15000.00</TotalAmount>\n" +
                    "    <Currency>INR</Currency>\n" +
                    "    <Status>{{STATUS}}</Status>\n" +
                    "    <LineItems>\n" +
                    "        <LineItem>\n" +
                    "            <ItemNumber>1</ItemNumber>\n" +
                    "            <Material>MAT-001</Material>\n" +
                    "            <Quantity>10</Quantity>\n" +
                    "            <UnitPrice>1000.00</UnitPrice>\n" +
                    "        </LineItem>\n" +
                    "        <LineItem>\n" +
                    "            <ItemNumber>2</ItemNumber>\n" +
                    "            <Material>MAT-002</Material>\n" +
                    "            <Quantity>5</Quantity>\n" +
                    "            <UnitPrice>1000.00</UnitPrice>\n" +
                    "        </LineItem>\n" +
                    "    </LineItems>\n" +
                    "</SalesOrder>";

    // Sample JSON payload (Java 11 compatible - using concatenation)
    private static final String SAMPLE_JSON_PAYLOAD =
            "{\n" +
                    "  \"orderId\": \"{{ORDER_ID}}\",\n" +
                    "  \"customerId\": \"CUST-{{CUST_ID}}\",\n" +
                    "  \"orderDate\": \"2025-12-04\",\n" +
                    "  \"totalAmount\": 15000.00,\n" +
                    "  \"currency\": \"INR\",\n" +
                    "  \"status\": \"{{STATUS}}\",\n" +
                    "  \"lineItems\": [\n" +
                    "    {\n" +
                    "      \"itemNumber\": 1,\n" +
                    "      \"material\": \"MAT-001\",\n" +
                    "      \"quantity\": 10,\n" +
                    "      \"unitPrice\": 1000.00\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"itemNumber\": 2,\n" +
                    "      \"material\": \"MAT-002\",\n" +
                    "      \"quantity\": 5,\n" +
                    "      \"unitPrice\": 1000.00\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

    @EventListener(ApplicationReadyEvent.class)
    public void generateSampleData() {
        System.out.println("🔄 Generating sample data with payloads...");

        for (int i = 1; i <= 20; i++) {
            IntegrationEvent event = new IntegrationEvent();
            event.setOrderId("PO-" + String.format("%05d", i));
            event.setStatus(STATUSES[random.nextInt(STATUSES.length)]);
            event.setIntegrationName(INTEGRATIONS[random.nextInt(INTEGRATIONS.length)]);
            event.setMessage(MESSAGES[random.nextInt(MESSAGES.length)]);

            int minutesAgo = random.nextInt(60) + 1;
            event.setTimestamp(LocalDateTime.now().minusMinutes(minutesAgo));

            // Add payload only to FAILED and PENDING events
            if (!event.getStatus().equals("SUCCESS")) {
                // Randomly choose XML or JSON
                boolean isXml = random.nextBoolean();

                if (isXml) {
                    String xmlPayload = SAMPLE_XML_PAYLOAD
                            .replace("{{ORDER_ID}}", event.getOrderId())
                            .replace("{{CUST_ID}}", String.format("%03d", i))
                            .replace("{{STATUS}}", event.getStatus());
                    event.setPayload(xmlPayload);
                    event.setPayloadFormat("XML");
                } else {
                    String jsonPayload = SAMPLE_JSON_PAYLOAD
                            .replace("{{ORDER_ID}}", event.getOrderId())
                            .replace("{{CUST_ID}}", String.format("%03d", i))
                            .replace("{{STATUS}}", event.getStatus());
                    event.setPayload(jsonPayload);
                    event.setPayloadFormat("JSON");
                }

                // Store original payload
                event.setOriginalPayload(event.getPayload());
                event.setRetryCount(0);
            }

            if (event.getStatus().equals("FAILED")) {
                event.setErrorDetails(ERROR_MESSAGES[random.nextInt(ERROR_MESSAGES.length)]);
            } else {
                event.setErrorDetails("");
            }

            repository.save(event);
            System.out.println("✓ Created: " + event.getOrderId() + " - " + event.getStatus() +
                    (event.getPayload() != null ? " (Payload: " + event.getPayloadFormat() + ")" : ""));
        }

        System.out.println("\n✅ Sample data generated successfully!");
        System.out.println("📊 Total events created: 20");
        System.out.println("🌐 Open browser: http://localhost:8080\n");
    }
}
