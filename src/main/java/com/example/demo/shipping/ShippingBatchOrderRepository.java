package com.example.demo.shipping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingBatchOrderRepository extends JpaRepository<ShippingBatchOrder, ShippingBatchOrderEmbeddedId> {

    @Query("SELECT sbo FROM ShippingBatchOrder sbo WHERE sbo.shippingBatch.id = :batchId")
    List<ShippingBatchOrder> findByBatchId(@Param("batchId") Long batchId);

    @Query("SELECT sbo FROM ShippingBatchOrder sbo WHERE sbo.order.id = :orderId")
    Optional<ShippingBatchOrder> findByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT sbo FROM ShippingBatchOrder sbo WHERE sbo.shippingBatch.id = :batchId AND sbo.order.id = :orderId")
    Optional<ShippingBatchOrder> findByBatchIdAndOrderId(@Param("batchId") Long batchId, @Param("orderId") Long orderId);

    @Query("SELECT COUNT(sbo) FROM ShippingBatchOrder sbo WHERE sbo.shippingBatch.id = :batchId")
    Long countByBatchId(@Param("batchId") Long batchId);

    @Query("SELECT sbo.order.id FROM ShippingBatchOrder sbo WHERE sbo.shippingBatch.id = :batchId")
    List<Long> findOrderIdsByBatchId(@Param("batchId") Long batchId);

    boolean existsByOrderId(Long orderId);
}