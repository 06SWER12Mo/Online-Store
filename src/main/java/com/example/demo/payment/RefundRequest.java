package com.example.demo.payment;

import jakarta.validation.constraints.NotBlank;

public class RefundRequest {

    @NotBlank
    private String transactionReference;

    private String reason;

    public RefundRequest() {}

    public String getTransactionReference() { return transactionReference; }
    public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}