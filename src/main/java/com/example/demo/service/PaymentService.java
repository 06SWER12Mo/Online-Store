package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.dto.request.PaymentRequest;
import com.example.demo.dto.request.RefundRequest;
import com.example.demo.dto.response.PaymentResponse;
import com.example.demo.enums.PaymentStatus;

public interface PaymentService {

    PaymentResponse processPayment(PaymentRequest request, Long userId);

    Optional<PaymentResponse> getPaymentById(Long id, Long userId, boolean isAdminOrManager);

    Optional<PaymentResponse> getPaymentByTransactionReference(String transactionReference, Long userId, boolean isAdminOrManager);

    Optional<PaymentResponse> getPaymentByOrderId(Long orderId, Long userId, boolean isAdminOrManager);

    PaymentResponse confirmPayment(String transactionReference);

    PaymentResponse refundPayment(RefundRequest request, Long userId, boolean isAdminOrManager);

    List<PaymentResponse> getAllPayments();

    List<PaymentResponse> getPaymentsByStatus(PaymentStatus status);
}