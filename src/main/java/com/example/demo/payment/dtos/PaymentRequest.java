package com.example.demo.payment.dtos;

import jakarta.validation.constraints.NotNull;
import com.example.demo.payment.PaymentMethod;

public class PaymentRequest {

    @NotNull(message = "Order ID is required to process payment")
    private Long orderId;

    @NotNull(message = "Please select a payment method")
    private PaymentMethod paymentMethod;

    public PaymentRequest() {}

    public PaymentRequest(Long orderId, PaymentMethod paymentMethod) {
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}