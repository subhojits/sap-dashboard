package com.example.sapdashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan(basePackages = {"com.example.sapdashboard.model"})
@EnableJpaRepositories(basePackages = {"com.example.sapdashboard.repository"})
@ComponentScan(basePackages = {"com.example.sapdashboard"})
@EnableScheduling
public class SapDashboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(SapDashboardApplication.class, args);
        System.out.println("🚀 SAP Integration Event Dashboard is running!");
        System.out.println("📊 Open browser: http://localhost:8080");
    }
}
