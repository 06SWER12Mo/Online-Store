package com.example.demo.payment;

import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toPaymentResponse(Payment payment) {
        if (payment == null) return null;

        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getId());
        response.setOrderId(payment.getOrder() != null ? payment.getOrder().getId() : null);
        response.setAmount(payment.getAmount());
        response.setTransactionId(payment.getTransactionReference());
        response.setPaymentMethod(payment.getMethod());
        response.setStatus(payment.getStatus());
        response.setPaymentDate(payment.getPaidAt());

        return response;
    }

    public Payment toPaymentEntity(PaymentRequest request) {
        if (request == null) return null;

        Payment payment = new Payment();
        payment.setAmount(request.getAmount());
        payment.setMethod(request.getPaymentMethod());
        payment.setStatus(PaymentStatus.Pending);

        return payment;
    }
}