package com.example.demo.mapper;

import org.springframework.stereotype.Component;

import com.example.demo.dto.request.PaymentRequest;
import com.example.demo.dto.response.PaymentResponse;
import com.example.demo.entity.Payment;
import com.example.demo.enums.PaymentStatus;

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
        // Amount will be set from the order in the service
        payment.setMethod(request.getPaymentMethod());
        payment.setStatus(PaymentStatus.PENDING);

        return payment;
    }
}