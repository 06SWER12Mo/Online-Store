package com.example.demo.shipping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingBatchRepository extends JpaRepository<ShippingBatch, Long> {

    List<ShippingBatch> findByStatus(ShippingStatus status);

    @Query("SELECT sb FROM ShippingBatch sb WHERE sb.bigArea.id = :bigAreaId")
    List<ShippingBatch> findByBigAreaId(@Param("bigAreaId") Long bigAreaId);

    @Query("SELECT sb FROM ShippingBatch sb WHERE sb.status = 'COLLECTING_ORDERS' AND sb.bigArea.id = :bigAreaId")
    Optional<ShippingBatch> findCollectingBatchByBigAreaId(@Param("bigAreaId") Long bigAreaId);

    @Query("SELECT sb FROM ShippingBatch sb WHERE sb.status = 'READY_TO_DISPATCH'")
    List<ShippingBatch> findReadyToDispatchBatches();

    @Query("SELECT sb FROM ShippingBatch sb WHERE sb.status = 'DISPATCHED'")
    List<ShippingBatch> findDispatchedBatches();

    @Query("SELECT sb FROM ShippingBatch sb WHERE sb.status = 'DISPATCHED' AND sb.autoDeliverAt < :now")
    List<ShippingBatch> findBatchesToAutoDeliver(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(sbo) FROM ShippingBatchOrder sbo WHERE sbo.shippingBatch.id = :batchId")
    Long countOrdersInBatch(@Param("batchId") Long batchId);
}