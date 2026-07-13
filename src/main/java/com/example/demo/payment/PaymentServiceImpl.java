package com.example.demo.payment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.order.Order;
import com.example.demo.order.OrderRepository;
import com.example.demo.order.OrderService;
import com.example.demo.order.OrderStatus;
import com.example.demo.payment.dtos.PaymentRequest;
import com.example.demo.payment.dtos.PaymentResponse;
import com.example.demo.payment.dtos.RefundRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;
    private final OrderService orderService;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              OrderRepository orderRepository,
                              PaymentMapper paymentMapper,
                              OrderService orderService) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.paymentMapper = paymentMapper;
        this.orderService = orderService;
    }

    // ========== ✅ PROCESS PAYMENT (MOCK - ALWAYS SUCCESSFUL) ==========

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        // 1. Find order
        Order order = orderRepository.findById(request.getOrderId())
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + request.getOrderId()));

        // 2. Check if already paid
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new RuntimeException("Order is already paid");
        }

        // 3. Create payment record
        Payment payment = paymentMapper.toPaymentEntity(request);
        payment.setOrder(order);
        
        // ✅ MOCK: Always successful
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);

        // 4. Update order
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setOrderStatus(OrderStatus.PAID);
        orderRepository.save(order);

        // 5. ✅ TRIGGER SHIPPING!
        orderService.markOrderReadyForShipping(order.getId());
        System.out.println("📢 Order " + order.getOrderNumber() + " paid and ready for shipping!");

        return paymentMapper.toPaymentResponse(savedPayment);
    }

    // ========== ✅ CONFIRM PAYMENT (MOCK - Just returns the payment) ==========

    @Override
    public PaymentResponse confirmPayment(String transactionReference) {
        Payment payment = paymentRepository.findByTransactionReference(transactionReference)
            .orElseThrow(() -> new RuntimeException("Payment not found with reference: " + transactionReference));

        // ✅ MOCK: Already confirmed during processPayment
        return paymentMapper.toPaymentResponse(payment);
    }

    // ========== ✅ REFUND PAYMENT (MOCK - Just updates status) ==========

    @Override
    public PaymentResponse refundPayment(RefundRequest request) {
        Payment payment = paymentRepository.findByTransactionReference(request.getTransactionReference())
            .orElseThrow(() -> new RuntimeException("Payment not found with reference: " + request.getTransactionReference()));

        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new RuntimeException("Only paid payments can be refunded");
        }

        payment.setStatus(PaymentStatus.REFUNDED);

        Order order = payment.getOrder();
        if (order != null) {
            order.setPaymentStatus(PaymentStatus.REFUNDED);
            order.setOrderStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        }

        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toPaymentResponse(savedPayment);
    }

    // ========== GETTER METHODS ==========

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
}