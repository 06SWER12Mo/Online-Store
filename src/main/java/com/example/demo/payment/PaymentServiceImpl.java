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

import java.math.BigDecimal;

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

        // 3. Check if order is in a valid state for payment
        if (order.getOrderStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new RuntimeException("Order is not in PENDING_PAYMENT status. Current status: " + order.getOrderStatus());
        }

        // 4. Validate that the order has items
        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            throw new RuntimeException("Order has no items to pay for");
        }

        // 5. Get the amount from the order (not from the request)
        BigDecimal amount = order.getTotalPrice();
        
        // 6. Validate amount is positive
        if (amount == null || amount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invalid order total amount: " + amount);
        }

        // 7. Create payment record
        Payment payment = paymentMapper.toPaymentEntity(request);
        payment.setOrder(order);
        payment.setAmount(amount); // Set amount from order
        
        // ✅ MOCK: Always successful
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);

        // 8. Update order
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setOrderStatus(OrderStatus.PAID);
        orderRepository.save(order);

        // 9. ✅ TRIGGER SHIPPING!
        try {
            orderService.markOrderReadyForShipping(order.getId());
            System.out.println("📢 Order " + order.getOrderNumber() + " paid and ready for shipping!");
        } catch (Exception e) {
            // Log the error but don't fail the payment
            System.err.println("⚠️ Failed to mark order ready for shipping: " + e.getMessage());
            // You might want to handle this differently based on your requirements
        }

        return paymentMapper.toPaymentResponse(savedPayment);
    }

    // ========== ✅ CONFIRM PAYMENT (MOCK - Just returns the payment) ==========

    @Override
    public PaymentResponse confirmPayment(String transactionReference) {
        Payment payment = paymentRepository.findByTransactionReference(transactionReference)
            .orElseThrow(() -> new RuntimeException("Payment not found with reference: " + transactionReference));

        // Check if payment is already confirmed
        if (payment.getStatus() == PaymentStatus.PAID) {
            return paymentMapper.toPaymentResponse(payment);
        }

        // If payment is pending, confirm it
        if (payment.getStatus() == PaymentStatus.PENDING) {
            payment.setStatus(PaymentStatus.PAID);
            payment.setPaidAt(LocalDateTime.now());
            Payment savedPayment = paymentRepository.save(payment);
            
            // Update the order
            Order order = payment.getOrder();
            if (order != null) {
                order.setPaymentStatus(PaymentStatus.PAID);
                order.setOrderStatus(OrderStatus.PAID);
                orderRepository.save(order);
                
                // Trigger shipping
                try {
                    orderService.markOrderReadyForShipping(order.getId());
                    System.out.println("📢 Order " + order.getOrderNumber() + " confirmed and ready for shipping!");
                } catch (Exception e) {
                    System.err.println("⚠️ Failed to mark order ready for shipping: " + e.getMessage());
                }
            }
            
            return paymentMapper.toPaymentResponse(savedPayment);
        }

        throw new RuntimeException("Payment cannot be confirmed in its current state: " + payment.getStatus());
    }

    // ========== ✅ REFUND PAYMENT ==========

    @Override
    public PaymentResponse refundPayment(RefundRequest request) {
        // 1. Find the payment
        Payment payment = paymentRepository.findByTransactionReference(request.getTransactionReference())
            .orElseThrow(() -> new RuntimeException("Payment not found with reference: " + request.getTransactionReference()));

        // 2. Validate payment can be refunded
        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new RuntimeException("Only paid payments can be refunded. Current status: " + payment.getStatus());
        }

        // 3. Check if payment is too old to refund (optional - e.g., 30 days limit)
        if (payment.getPaidAt() != null && 
            payment.getPaidAt().isBefore(LocalDateTime.now().minusDays(30))) {
            throw new RuntimeException("Payment is older than 30 days and cannot be refunded");
        }

        // 4. Update payment status
        payment.setStatus(PaymentStatus.REFUNDED);
        Payment savedPayment = paymentRepository.save(payment);

        // 5. Update the associated order
        Order order = payment.getOrder();
        if (order != null) {
            // Check if order can be cancelled
            if (!order.getOrderStatus().canBeCancelled()) {
                throw new RuntimeException("Order cannot be cancelled in its current status: " + order.getOrderStatus());
            }
            
            order.setPaymentStatus(PaymentStatus.REFUNDED);
            order.setOrderStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            
            System.out.println("🔄 Order " + order.getOrderNumber() + " cancelled due to refund");
        }

        System.out.println("💰 Payment refunded: " + payment.getTransactionReference() + 
                          " - Reason: " + (request.getReason() != null ? request.getReason() : "No reason provided"));
        
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

    // ========== ADDITIONAL HELPER METHODS (Optional but useful) ==========

    /**
     * Get payment statistics
     */
    @Transactional(readOnly = true)
    public PaymentStatistics getPaymentStatistics() {
        List<Payment> allPayments = paymentRepository.findAll();
        
        long totalPayments = allPayments.size();
        long paidPayments = allPayments.stream().filter(p -> p.getStatus() == PaymentStatus.PAID).count();
        long pendingPayments = allPayments.stream().filter(p -> p.getStatus() == PaymentStatus.PENDING).count();
        long refundedPayments = allPayments.stream().filter(p -> p.getStatus() == PaymentStatus.REFUNDED).count();
        long failedPayments = allPayments.stream().filter(p -> p.getStatus() == PaymentStatus.FAILED).count();
        
        java.math.BigDecimal totalRevenue = allPayments.stream()
            .filter(p -> p.getStatus() == PaymentStatus.PAID)
            .map(Payment::getAmount)
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        
        return new PaymentStatistics(totalPayments, paidPayments, pendingPayments, 
                                    refundedPayments, failedPayments, totalRevenue);
    }

    /**
     * Check if payment exists by transaction reference
     */
    @Transactional(readOnly = true)
    public boolean paymentExists(String transactionReference) {
        return paymentRepository.existsByTransactionReference(transactionReference);
    }

    /**
     * Inner class for payment statistics
     */
    public static class PaymentStatistics {
        private final long totalPayments;
        private final long paidPayments;
        private final long pendingPayments;
        private final long refundedPayments;
        private final long failedPayments;
        private final java.math.BigDecimal totalRevenue;

        public PaymentStatistics(long totalPayments, long paidPayments, long pendingPayments,
                                 long refundedPayments, long failedPayments, java.math.BigDecimal totalRevenue) {
            this.totalPayments = totalPayments;
            this.paidPayments = paidPayments;
            this.pendingPayments = pendingPayments;
            this.refundedPayments = refundedPayments;
            this.failedPayments = failedPayments;
            this.totalRevenue = totalRevenue;
        }

        // Getters
        public long getTotalPayments() { return totalPayments; }
        public long getPaidPayments() { return paidPayments; }
        public long getPendingPayments() { return pendingPayments; }
        public long getRefundedPayments() { return refundedPayments; }
        public long getFailedPayments() { return failedPayments; }
        public java.math.BigDecimal getTotalRevenue() { return totalRevenue; }
    }
}