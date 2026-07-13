package com.example.demo.shipping;

import java.util.List;

import com.example.demo.shipping.dtos.AssignBusRequest;
import com.example.demo.shipping.dtos.DeliveryConfirmationRequest;
import com.example.demo.shipping.dtos.ShippingBatchResponse;

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

    // ✅ Bus assignment (manual)
    ShippingBatchResponse assignBusToBatch(AssignBusRequest request);

    // ✅ Bus assignment (auto)
    ShippingBatchResponse autoAssignBus(Long batchId);
    Bus getFirstAvailableBus();

    // Batch lifecycle
    ShippingBatchResponse markBatchReadyToDispatch(Long batchId);
    ShippingBatchResponse dispatchBatch(Long batchId);
    ShippingBatchResponse autoDeliverBatch(Long batchId);
    List<ShippingBatch> getBatchesToAutoDeliver();
    ShippingBatchResponse confirmDelivery(DeliveryConfirmationRequest request);
    ShippingBatchResponse cancelBatch(Long batchId);

    // Order tracking within batch
    boolean isOrderInBatch(Long orderId);
    ShippingBatchResponse getBatchByOrderId(Long orderId);
}