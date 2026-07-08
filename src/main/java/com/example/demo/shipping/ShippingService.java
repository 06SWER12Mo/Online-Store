package com.example.demo.shipping;

import java.util.List;

public interface ShippingService {

    // Batch operations
    ShippingBatchResponse createBatch(Long bigAreaId, Integer minimumOrders);

    ShippingBatchResponse getBatchById(Long id);

    List<ShippingBatchResponse> getAllBatches();

    List<ShippingBatchResponse> getBatchesByStatus(ShippingStatus status);

    List<ShippingBatchResponse> getBatchesByBigAreaId(Long bigAreaId);

    // Order assignment
    ShippingBatchResponse addOrderToBatch(Long batchId, Long orderId);

    ShippingBatchResponse removeOrderFromBatch(Long batchId, Long orderId);

    // Bus assignment
    ShippingBatchResponse assignBusToBatch(AssignBusRequest request);

    // Batch lifecycle
    ShippingBatchResponse markBatchReadyToDispatch(Long batchId);

    ShippingBatchResponse dispatchBatch(Long batchId);

    ShippingBatchResponse confirmDelivery(DeliveryConfirmationRequest request);

    ShippingBatchResponse cancelBatch(Long batchId);

    // Order tracking within batch
    boolean isOrderInBatch(Long orderId);

    ShippingBatchResponse getBatchByOrderId(Long orderId);
}