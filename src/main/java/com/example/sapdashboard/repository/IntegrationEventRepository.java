package com.example.sapdashboard.repository;

import com.example.sapdashboard.model.IntegrationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IntegrationEventRepository extends JpaRepository<IntegrationEvent, Long> {

    List<IntegrationEvent> findByStatusOrderByTimestampDesc(String status);

    List<IntegrationEvent> findByOrderIdContainingIgnoreCaseOrderByTimestampDesc(String orderId);

    List<IntegrationEvent> findTop50ByOrderByTimestampDesc();

    Long countByStatus(String status);

    List<IntegrationEvent> findByIntegrationNameOrderByTimestampDesc(String integrationName);

    //Long count();

    @Query("SELECT e FROM IntegrationEvent e WHERE e.timestamp BETWEEN :startTime AND :endTime ORDER BY e.timestamp DESC")
    List<IntegrationEvent> findEventsBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
