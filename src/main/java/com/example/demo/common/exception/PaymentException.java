package com.example.demo.common.exception;

public class PaymentException extends RuntimeException {

    private final String transactionId;
    private final String paymentMethod;

    public PaymentException(String message) {
        super(message);
        this.transactionId = null;
        this.paymentMethod = null;
    }

    public PaymentException(String message, String transactionId) {
        super(message);
        this.transactionId = transactionId;
        this.paymentMethod = null;
    }

    public PaymentException(String message, String transactionId, String paymentMethod) {
        super(String.format("Payment failed: %s (Transaction: %s, Method: %s)",
                message, transactionId, paymentMethod));
        this.transactionId = transactionId;
        this.paymentMethod = paymentMethod;
    }

    public PaymentException(String message, Throwable cause) {
        super(message, cause);
        this.transactionId = null;
        this.paymentMethod = null;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}