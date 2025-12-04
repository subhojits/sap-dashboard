package com.example.sapdashboard.repository;

import com.example.sapdashboard.model.IntegrationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IntegrationEventRepository extends JpaRepository<IntegrationEvent, Long> {

    // --- EXISTING QUERIES (FIXED) ---
    @Query("SELECT e FROM IntegrationEvent e WHERE e.status = 'FAILED' AND e.payload IS NOT NULL ORDER BY e.createdAt DESC")
    List<IntegrationEvent> findFailedEventsWithPayload();

    // --- NEW METHODS ADDED (FROM ERROR LIST) ---

    // Fixes: cannot find symbol method findByStatus(java.lang.String)
    List<IntegrationEvent> findByStatus(String status);

    // Fixes: cannot find symbol method findByOrderId(java.lang.String)
    List<IntegrationEvent> findByOrderId(String orderId);

    // Fixes: cannot find symbol method findFailedEventById(java.lang.Long)
    @Query("SELECT e FROM IntegrationEvent e WHERE e.id = :id AND e.status = 'FAILED'")
    Optional<IntegrationEvent> findFailedEventById(@Param("id") Long id);

    // --- ADDITIONAL USEFUL QUERIES ---
    @Query("SELECT e FROM IntegrationEvent e WHERE e.status = 'SUCCESS' ORDER BY e.createdAt DESC")
    List<IntegrationEvent> findSuccessfulEvents();

    @Query("SELECT e FROM IntegrationEvent e WHERE e.status = 'PENDING' ORDER BY e.createdAt DESC")
    List<IntegrationEvent> findPendingEvents();

    @Query("SELECT e FROM IntegrationEvent e WHERE e.retryCount > 0 ORDER BY e.createdAt DESC")
    List<IntegrationEvent> findRetriedEvents();

    @Query("SELECT COUNT(e) FROM IntegrationEvent e WHERE e.status = 'FAILED'")
    long countFailedEvents();

    @Query("SELECT COUNT(e) FROM IntegrationEvent e WHERE e.status = 'SUCCESS'")
    long countSuccessfulEvents();
}
