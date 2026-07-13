package com.example.demo.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface StoreSettingsRepository extends JpaRepository<StoreSettings, Long> {

    // Get the first (and only) settings record
    @Query("SELECT s FROM StoreSettings s WHERE s.id = (SELECT MIN(s2.id) FROM StoreSettings s2)")
    Optional<StoreSettings> findFirst();

    // Check if maintenance mode is enabled
    @Query("SELECT s.maintenanceMode FROM StoreSettings s WHERE s.id = (SELECT MIN(s2.id) FROM StoreSettings s2)")
    boolean getMaintenanceMode();

    // Check if registration is allowed
    @Query("SELECT s.allowRegistration FROM StoreSettings s WHERE s.id = (SELECT MIN(s2.id) FROM StoreSettings s2)")
    boolean getAllowRegistration();

    // Update maintenance mode
    @Modifying
    @Transactional
    @Query("UPDATE StoreSettings s SET s.maintenanceMode = :enabled, s.maintenanceMessage = :message WHERE s.id = :id")
    void updateMaintenanceMode(@Param("id") Long id, @Param("enabled") boolean enabled, @Param("message") String message);
}