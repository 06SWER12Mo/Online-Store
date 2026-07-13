package com.example.demo.shipping.dtos;

import jakarta.validation.constraints.NotNull;

public class AssignBusRequest {

    @NotNull
    private Long batchId;

    @NotNull
    private Long busId;

    public AssignBusRequest() {}

    public AssignBusRequest(Long batchId, Long busId) {
        this.batchId = batchId;
        this.busId = busId;
    }

    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }

    public Long getBusId() { return busId; }
    public void setBusId(Long busId) { this.busId = busId; }
}