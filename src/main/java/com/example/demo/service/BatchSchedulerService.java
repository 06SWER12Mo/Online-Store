package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.response.ShippingBatchResponse;

public interface BatchSchedulerService {

    void autoCreateBatchesForBigAreas();

    void checkAndMarkBatchesReadyToDispatch();

    void autoDispatchReadyBatches();

    void autoDeliverBatches();

    List<ShippingBatchResponse> getPendingDispatches();

    void processBatchDispatch(Long batchId);
}