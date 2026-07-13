package com.example.demo.shipping;

import java.util.List;

import com.example.demo.shipping.dtos.ShippingBatchResponse;

public interface BatchSchedulerService {

    void autoCreateBatchesForBigAreas();

    void checkAndMarkBatchesReadyToDispatch();

    void autoDispatchReadyBatches();

    void autoDeliverBatches();

    List<ShippingBatchResponse> getPendingDispatches();

    void processBatchDispatch(Long batchId);
}