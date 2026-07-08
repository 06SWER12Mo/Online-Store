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

    Optional<StoreSettings> findFirstByOrderByIdAsc();

    @Query("SELECT s FROM StoreSettings s WHERE s.id = (SELECT MIN(s2.id) FROM StoreSettings s2)")
    Optional<StoreSettings> findFirst();

    @Modifying
    @Transactional
    @Query("UPDATE StoreSettings s SET s.maintenanceMode = :maintenanceMode, s.maintenanceMessage = :message WHERE s.id = :id")
    void updateMaintenanceMode(@Param("id") Long id, @Param("maintenanceMode") boolean maintenanceMode, @Param("message") String message);

    @Modifying
    @Transactional
    @Query("UPDATE StoreSettings s SET s.updatedBy = :updatedBy WHERE s.id = :id")
    void updateUpdatedBy(@Param("id") Long id, @Param("updatedBy") String updatedBy);

    @Query("SELECT s.maintenanceMode FROM StoreSettings s WHERE s.id = (SELECT MIN(s2.id) FROM StoreSettings s2)")
    boolean getMaintenanceMode();

    @Query("SELECT s.allowRegistration FROM StoreSettings s WHERE s.id = (SELECT MIN(s2.id) FROM StoreSettings s2)")
    boolean getAllowRegistration();
}