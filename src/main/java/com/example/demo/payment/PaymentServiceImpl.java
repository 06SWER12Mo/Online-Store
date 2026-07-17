package com.example.demo.payment;

import com.example.demo.inventory.InventoryService;
import com.example.demo.inventory.InventoryTransactionType;
import com.example.demo.order.Order;
import com.example.demo.order.OrderItem;
import com.example.demo.order.OrderRepository;
import com.example.demo.order.OrderService;
import com.example.demo.order.OrderStatus;
import com.example.demo.payment.dtos.PaymentRequest;
import com.example.demo.payment.dtos.PaymentResponse;
import com.example.demo.payment.dtos.RefundRequest;
import com.example.demo.product.Product;
import com.example.demo.product.ProductRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final BigDecimal REFUND_FEE_RATE = new BigDecimal("0.33"); // 1/3 fee for users
    
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;
    private final OrderService orderService;
    private final InventoryService inventoryService;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              OrderRepository orderRepository,
                              ProductRepository productRepository,
                              UserRepository userRepository,
                              PaymentMapper paymentMapper,
                              OrderService orderService,
                              InventoryService inventoryService) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.paymentMapper = paymentMapper;
        this.orderService = orderService;
        this.inventoryService = inventoryService;
    }

    // ========== PROCESS PAYMENT ==========

    @Override
    public PaymentResponse processPayment(PaymentRequest request, Long userId) {
        // 1. Find order
        Order order = orderRepository.findById(request.getOrderId())
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + request.getOrderId()));

        // 2. Check if order belongs to user
        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only pay for your own orders");
        }

        // 3. Check if already paid
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new RuntimeException("Order is already paid");
        }

        // 4. Check if order is in a valid state for payment
        if (order.getOrderStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new RuntimeException("Order is not in PENDING_PAYMENT status. Current status: " + order.getOrderStatus());
        }

        // 5. Validate that the order has items
        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            throw new RuntimeException("Order has no items to pay for");
        }

        // 6. Get the amount from the order
        BigDecimal amount = order.getTotalPrice();
        
        // 7. Validate amount is positive
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invalid order total amount: " + amount);
        }

        // 8. Create payment record
        Payment payment = paymentMapper.toPaymentEntity(request);
        payment.setOrder(order);
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);

        // 9. Update order
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setOrderStatus(OrderStatus.PAID);
        orderRepository.save(order);

        // 10. TRIGGER SHIPPING!
        try {
            orderService.markOrderReadyForShipping(order.getId());
            System.out.println("📢 Order " + order.getOrderNumber() + " paid and ready for shipping!");
        } catch (Exception e) {
            System.err.println("⚠️ Failed to mark order ready for shipping: " + e.getMessage());
        }

        return paymentMapper.toPaymentResponse(savedPayment);
    }

    // ========== REFUND PAYMENT ==========

    @Override
    public PaymentResponse refundPayment(RefundRequest request, Long userId, boolean isAdminOrManager) {
        // 1. Find the order
        Order order = orderRepository.findById(request.getOrderId())
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + request.getOrderId()));

        // 2. Find the payment associated with the order
        Payment payment = paymentRepository.findByOrderId(order.getId())
            .orElseThrow(() -> new RuntimeException("No payment found for order: " + request.getOrderId()));

        // 3. Validate payment can be refunded
        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new RuntimeException("Only paid payments can be refunded. Current status: " + payment.getStatus());
        }

        // 4. Check if already refunded
        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new RuntimeException("Payment has already been refunded");
        }

        // 5. Check if order is in a cancellable state
        if (!order.getOrderStatus().canBeCancelled()) {
            throw new RuntimeException("Order cannot be refunded in its current status: " + order.getOrderStatus());
        }

        // 6. 🔑 PERMISSION CHECK
        if (!isAdminOrManager) {
            // Regular user - check if order belongs to them
            if (!order.getUser().getId().equals(userId)) {
                throw new RuntimeException("You can only refund your own orders");
            }
            
            // ✅ User can only refund if order is NOT shipped or delivered
            if (order.getOrderStatus() == OrderStatus.SHIPPED || 
                order.getOrderStatus() == OrderStatus.DELIVERED) {
                throw new RuntimeException("Cannot refund order that is already shipped or delivered");
            }
        }

        // 7. Calculate refund amount
        BigDecimal originalAmount = payment.getAmount();
        BigDecimal refundAmount;
        BigDecimal feeAmount = BigDecimal.ZERO;
        String refundType;
        
        if (isAdminOrManager) {
            //  Admin/Manager: 100% refund
            refundAmount = originalAmount;
            refundType = "FULL";
            System.out.println("💰 Admin/Manager refund: 100% - $" + refundAmount);
        } else {
            //  Regular User: 2/3 refund (33% fee)
            feeAmount = originalAmount.multiply(REFUND_FEE_RATE)
                .setScale(2, RoundingMode.HALF_UP);
            refundAmount = originalAmount.subtract(feeAmount)
                .setScale(2, RoundingMode.HALF_UP);
            refundType = "PARTIAL (2/3)";
            System.out.println("💰 User refund: 2/3 - $" + refundAmount + " (Fee: $" + feeAmount + ")");
        }

        // 8. RESTORE INVENTORY - ONLY THROUGH INVENTORY SERVICE
        System.out.println("🔄 Restoring inventory for Order: " + order.getOrderNumber());
        
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
           
            inventoryService.createInventoryTransaction(
                product.getId(),
                InventoryTransactionType.RETURN,
                item.getQuantity(),
                order.getId(),
                userId,
                "Stock restored due to refund | Order: " + order.getOrderNumber() +
                " | Product: " + product.getName() +
                " | Quantity: " + item.getQuantity() +
                " | Refund Type: " + refundType +
                " | Reason: " + (request.getReason() != null ? request.getReason() : "No reason provided")
            );
            
            System.out.println("✅ Restored " + item.getQuantity() + " units of " + product.getName());
        }

        // 9. Update payment status to REFUNDED
        payment.setStatus(PaymentStatus.REFUNDED);
        Payment savedPayment = paymentRepository.save(payment);

        // 10. Update order status
        order.setPaymentStatus(PaymentStatus.REFUNDED);
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        // 11. Log the refund details
        System.out.println("💰 Refund completed:");
        System.out.println("   - Order: " + order.getOrderNumber());
        System.out.println("   - Original Amount: $" + originalAmount);
        System.out.println("   - Refund Amount: $" + refundAmount);
        System.out.println("   - Fee Amount: $" + feeAmount);
        System.out.println("   - Refund Type: " + refundType);
        System.out.println("   - Reason: " + (request.getReason() != null ? request.getReason() : "No reason provided"));

        return paymentMapper.toPaymentResponse(savedPayment);
    }

    // ========== GET PAYMENT BY ID ==========

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentResponse> getPaymentById(Long id, Long userId, boolean isAdminOrManager) {
        Optional<Payment> paymentOpt = paymentRepository.findById(id);
        
        if (paymentOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Payment payment = paymentOpt.get();
        
        // Check permission - user can only see their own payments unless admin/manager
        if (!isAdminOrManager && !payment.getOrder().getUser().getId().equals(userId)) {
            return Optional.empty();
        }
        
        return paymentOpt.map(paymentMapper::toPaymentResponse);
    }

    // ========== GET PAYMENT BY TRANSACTION REFERENCE ==========

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentResponse> getPaymentByTransactionReference(String transactionReference, Long userId, boolean isAdminOrManager) {
        Optional<Payment> paymentOpt = paymentRepository.findByTransactionReference(transactionReference);
        
        if (paymentOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Payment payment = paymentOpt.get();
        
        // Check permission - user can only see their own payments unless admin/manager
        if (!isAdminOrManager && !payment.getOrder().getUser().getId().equals(userId)) {
            return Optional.empty();
        }
        
        return paymentOpt.map(paymentMapper::toPaymentResponse);
    }

    // ========== GET PAYMENT BY ORDER ID ==========

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentResponse> getPaymentByOrderId(Long orderId, Long userId, boolean isAdminOrManager) {
        Optional<Payment> paymentOpt = paymentRepository.findByOrderId(orderId);
        
        if (paymentOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Payment payment = paymentOpt.get();
        
        // Check permission - user can only see their own payments unless admin/manager
        if (!isAdminOrManager && !payment.getOrder().getUser().getId().equals(userId)) {
            return Optional.empty();
        }
        
        return paymentOpt.map(paymentMapper::toPaymentResponse);
    }

    // ========== CONFIRM PAYMENT ==========

    @Override
    public PaymentResponse confirmPayment(String transactionReference) {
        Payment payment = paymentRepository.findByTransactionReference(transactionReference)
            .orElseThrow(() -> new RuntimeException("Payment not found with reference: " + transactionReference));

        if (payment.getStatus() == PaymentStatus.PAID) {
            return paymentMapper.toPaymentResponse(payment);
        }

        if (payment.getStatus() == PaymentStatus.PENDING) {
            payment.setStatus(PaymentStatus.PAID);
            payment.setPaidAt(LocalDateTime.now());
            Payment savedPayment = paymentRepository.save(payment);
            
            Order order = payment.getOrder();
            if (order != null) {
                order.setPaymentStatus(PaymentStatus.PAID);
                order.setOrderStatus(OrderStatus.PAID);
                orderRepository.save(order);
                
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

    // ========== GET ALL PAYMENTS (Admin/Manager only) ==========

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
            .map(paymentMapper::toPaymentResponse)
            .collect(Collectors.toList());
    }

    // ========== GET PAYMENTS BY STATUS (Admin/Manager only) ==========

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findAll().stream()
            .filter(p -> p.getStatus() == status)
            .map(paymentMapper::toPaymentResponse)
            .collect(Collectors.toList());
    }
}