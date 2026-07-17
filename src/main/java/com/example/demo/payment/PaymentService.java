package com.example.demo.payment;

import java.util.List;
import java.util.Optional;

import com.example.demo.payment.dtos.PaymentRequest;
import com.example.demo.payment.dtos.PaymentResponse;
import com.example.demo.payment.dtos.RefundRequest;

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