package com.example.demo.shipping;

import jakarta.validation.constraints.NotNull;

public class DeliveryConfirmationRequest {

    @NotNull
    private Long batchId;

    private String notes;

    public DeliveryConfirmationRequest() {}

    public DeliveryConfirmationRequest(Long batchId, String notes) {
        this.batchId = batchId;
        this.notes = notes;
    }

    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}