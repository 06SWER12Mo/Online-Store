package com.example.demo.shipping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {

    Optional<Bus> findByPlateNumber(String plateNumber);

    List<Bus> findByIsActiveTrue();

    List<Bus> findByIsActiveFalse();

    @Query("SELECT b FROM Bus b WHERE b.bigArea.id = :bigAreaId AND b.isActive = true")
    List<Bus> findActiveBusesByBigAreaId(@Param("bigAreaId") Long bigAreaId);

    boolean existsByPlateNumber(String plateNumber);
}