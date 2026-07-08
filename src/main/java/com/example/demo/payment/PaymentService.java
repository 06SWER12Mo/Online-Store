package com.example.demo.payment;

import java.util.List;
import java.util.Optional;

public interface PaymentService {

    PaymentResponse processPayment(PaymentRequest request);

    Optional<PaymentResponse> getPaymentById(Long id);

    Optional<PaymentResponse> getPaymentByTransactionReference(String transactionReference);

    Optional<PaymentResponse> getPaymentByOrderId(Long orderId);

    PaymentResponse confirmPayment(String transactionReference);

    PaymentResponse refundPayment(RefundRequest request);

    List<PaymentResponse> getAllPayments();

    List<PaymentResponse> getPaymentsByStatus(PaymentStatus status);
}