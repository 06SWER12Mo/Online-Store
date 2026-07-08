package com.example.demo.shipping;

import java.util.List;

public interface BatchSchedulerService {

    void autoCreateBatchesForBigAreas();

    void autoDispatchReadyBatches();

    void checkAndMarkBatchesReadyToDispatch();

    List<ShippingBatchResponse> getPendingDispatches();

    void processBatchDispatch(Long batchId);
}