package com.example.demo.payment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.order.Order;
import com.example.demo.order.OrderRepository;
import com.example.demo.order.OrderStatus;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              OrderRepository orderRepository,
                              PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.paymentMapper = paymentMapper;
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
            .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getPaymentStatus() == PaymentStatus.Paid) {
            throw new RuntimeException("Order is already paid");
        }

        Payment payment = paymentMapper.toPaymentEntity(request);
        payment.setOrder(order);

        // Simulate payment processing
        boolean paymentSuccessful = simulatePaymentProcessing(payment);

        if (paymentSuccessful) {
            payment.setStatus(PaymentStatus.Paid);
            order.setPaymentStatus(PaymentStatus.Paid);
            order.setOrderStatus(OrderStatus.ReadyForShipping);
        } else {
            payment.setStatus(PaymentStatus.Failed);
        }

        Payment savedPayment = paymentRepository.save(payment);
        orderRepository.save(order);

        return paymentMapper.toPaymentResponse(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentResponse> getPaymentById(Long id) {
        return paymentRepository.findById(id)
            .map(paymentMapper::toPaymentResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentResponse> getPaymentByTransactionReference(String transactionReference) {
        return paymentRepository.findByTransactionReference(transactionReference)
            .map(paymentMapper::toPaymentResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentResponse> getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
            .map(paymentMapper::toPaymentResponse);
    }

    @Override
    public PaymentResponse confirmPayment(String transactionReference) {
        Payment payment = paymentRepository.findByTransactionReference(transactionReference)
            .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.Pending) {
            throw new RuntimeException("Payment is not in pending status");
        }

        payment.setStatus(PaymentStatus.Paid);

        Order order = payment.getOrder();
        if (order != null) {
            order.setPaymentStatus(PaymentStatus.Paid);
            order.setOrderStatus(OrderStatus.ReadyForShipping);
            orderRepository.save(order);
        }

        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toPaymentResponse(savedPayment);
    }

    @Override
    public PaymentResponse refundPayment(RefundRequest request) {
        Payment payment = paymentRepository.findByTransactionReference(request.getTransactionReference())
            .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.Paid) {
            throw new RuntimeException("Only paid payments can be refunded");
        }

        payment.setStatus(PaymentStatus.Refunded);

        Order order = payment.getOrder();
        if (order != null) {
            order.setPaymentStatus(PaymentStatus.Refunded);
            order.setOrderStatus(OrderStatus.Cancelled);
            orderRepository.save(order);
        }

        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toPaymentResponse(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
            .map(paymentMapper::toPaymentResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findAll().stream()
            .filter(p -> p.getStatus() == status)
            .map(paymentMapper::toPaymentResponse)
            .collect(Collectors.toList());
    }

    private boolean simulatePaymentProcessing(Payment payment) {
        // Simulate payment gateway call
        // In production, integrate with actual payment provider
        if (payment.getMethod() == PaymentMethod.CashOnDelivery) {
            return true; // COD always succeeds initially
        }
        // Credit card simulation - 95% success rate
        return Math.random() > 0.05;
    }
}