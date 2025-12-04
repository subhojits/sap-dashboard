package com.example.sapdashboard.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    // ===== TOPICS =====

    /**
     * Main integration events topic
     * Events are published here from the application
     */
    @Bean
    public NewTopic sapIntegrationEventsTopic() {
        return TopicBuilder.name("sap-integration-events")
                .partitions(1)
                .replicas(1)
                .compact()
                .build();
    }

    /**
     * Retry topic for failed events with updated payload
     * Retry events are sent here with edited payloads
     */
    @Bean
    public NewTopic sapIntegrationOrderRetryTopic() {
        return TopicBuilder.name("sap-integration-order-retry")
                .partitions(1)
                .replicas(1)
                .compact()
                .build();
    }

    // ===== PRODUCER FACTORY =====

    /**
     * Kafka Producer Factory configuration
     * Defines how to serialize messages being sent to Kafka
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        // Bootstrap servers
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Key serializer - String keys
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // Value serializer - JSON values (for complex objects)
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // Optional producer configurations
        configProps.put(ProducerConfig.ACKS_CONFIG, "all"); // Wait for all replicas
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3); // Retry 3 times on failure
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 10); // Wait 10ms to batch messages

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    // ===== KAFKA TEMPLATE =====

    /**
     * Kafka Template bean for sending messages
     * This is injected into services and controllers
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
