package com.example.sapdashboard.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

// @Configuration tells Spring this is a configuration class
@Configuration
public class KafkaConfig {

    // ===== CREATE KAFKA TOPIC =====
    // @Bean tells Spring to create this object and manage it
    @Bean
    public NewTopic sapIntegrationTopic() {
        // Create a new Kafka topic called "sap-integration-events"
        return TopicBuilder.name("sap-integration-events")
                // Number of partitions (1 = single partition)
                .partitions(1)
                // Number of replicas (1 = no backup copies)
                .replicas(1)
                .build();
    }
}
