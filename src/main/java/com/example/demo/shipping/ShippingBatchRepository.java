package com.example.demo.shipping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingBatchRepository extends JpaRepository<ShippingBatch, Long> {

    List<ShippingBatch> findByStatus(ShippingStatus status);

    @Query("SELECT sb FROM ShippingBatch sb WHERE sb.bigArea.id = :bigAreaId")
    List<ShippingBatch> findByBigAreaId(@Param("bigAreaId") Long bigAreaId);

    @Query("SELECT sb FROM ShippingBatch sb WHERE sb.bus.id = :busId")
    List<ShippingBatch> findByBusId(@Param("busId") Long busId);

    @Query("SELECT sb FROM ShippingBatch sb WHERE sb.status = :status AND sb.bigArea.id = :bigAreaId")
    List<ShippingBatch> findByStatusAndBigAreaId(@Param("status") ShippingStatus status, @Param("bigAreaId") Long bigAreaId);

    @Query("SELECT sb FROM ShippingBatch sb WHERE sb.status = 'CollectingOrders' AND sb.bigArea.id = :bigAreaId")
    Optional<ShippingBatch> findCollectingBatchByBigAreaId(@Param("bigAreaId") Long bigAreaId);

    @Query("SELECT COUNT(sbo) FROM ShippingBatchOrder sbo WHERE sbo.shippingBatch.id = :batchId")
    Long countOrdersInBatch(@Param("batchId") Long batchId);

    @Query("SELECT sb FROM ShippingBatch sb WHERE sb.status = 'ReadyToDispatch'")
    List<ShippingBatch> findReadyToDispatchBatches();
}