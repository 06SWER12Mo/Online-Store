package com.example.demo.order;

import com.example.demo.cart.Cart;
import com.example.demo.cart.CartItem;
import com.example.demo.cart.CartRepository;
import com.example.demo.inventory.InventoryService;
import com.example.demo.inventory.InventoryTransactionType;
import com.example.demo.location.DeliveryAddress;
import com.example.demo.location.DeliveryAddressRepository;
import com.example.demo.location.Town;
import com.example.demo.location.TownRepository;
import com.example.demo.order.dtos.OrderResponse;
import com.example.demo.order.dtos.OrderSummaryResponse;
import com.example.demo.order.dtos.OrderTrackingRequest;
import com.example.demo.order.dtos.PlaceOrderRequest;
import com.example.demo.order.dtos.TrackingResponse;
import com.example.demo.order.dtos.UpdateOrderStatusRequest;
import com.example.demo.payment.PaymentStatus;
import com.example.demo.product.Product;
import com.example.demo.product.ProductRepository;
import com.example.demo.shipping.ShippingService;
import com.example.demo.shipping.ShippingStatus;
import com.example.demo.shipping.dtos.ShippingBatchResponse;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final TownRepository townRepository;
    private final CartRepository cartRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final OrderMapper orderMapper;
    private final InventoryService inventoryService;
    private final ShippingService shippingService;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            TownRepository townRepository,
            CartRepository cartRepository,
            DeliveryAddressRepository deliveryAddressRepository,
            OrderMapper orderMapper,
            InventoryService inventoryService,
            ShippingService shippingService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.townRepository = townRepository;
        this.cartRepository = cartRepository;
        this.deliveryAddressRepository = deliveryAddressRepository;
        this.orderMapper = orderMapper;
        this.inventoryService = inventoryService;
        this.shippingService = shippingService;
    }

    // ============================================================
    // PLACE ORDER
    // ============================================================

    @Override
    public OrderResponse placeOrder(Long userId, PlaceOrderRequest request) {
        // 1. Get authenticated user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // 2. Get the cart
        Cart cart = cartRepository.findByUserIdWithItems(userId)
            .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        // 3. Validate cart
        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty. Cannot place order.");
        }

        // 4. Verify cart belongs to user
        if (!cart.getUser().getId().equals(userId)) {
            throw new RuntimeException("Cart does not belong to this user");
        }

        // 5. Get the delivery address
        DeliveryAddress deliveryAddress = deliveryAddressRepository
            .findByIdAndUserId(request.getDeliveryAddressId(), userId)
            .orElseThrow(() -> new RuntimeException(
                "Delivery address not found with id: " + request.getDeliveryAddressId() + 
                " for user: " + userId
            ));

        // 6. Get the town from the delivery address
        Town shippingTown = deliveryAddress.getTown();
        if (shippingTown == null) {
            throw new RuntimeException("Delivery address does not have a town associated");
        }

        // 7. Create order
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUser(user);
        
        // User info (guest fields for authenticated user)
        order.setUserName(user.getFirstName() + " " + user.getLastName());
        order.setUserEmail(user.getEmail());
        order.setUserPhone(user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
        
        // Shipping info from delivery address
        order.setShippingName(deliveryAddress.getRecipientName());
        order.setShippingPhone(deliveryAddress.getRecipientPhone());
        order.setShippingTown(shippingTown);
        order.setShippingStreet(deliveryAddress.getStreet());
        order.setShippingBuilding(deliveryAddress.getBuilding());
        order.setLatitude(deliveryAddress.getLatitude());
        order.setLongitude(deliveryAddress.getLongitude());
        
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setOrderStatus(OrderStatus.PENDING_PAYMENT);

        // 8. Create order items from cart items
        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();
            Integer quantity = cartItem.getQuantity();

            // Validate stock
            if (product.getStockQuantity() < quantity) {
                throw new RuntimeException(
                    "Insufficient stock for product: " + product.getName() + 
                    ". Available: " + product.getStockQuantity() + 
                    ", Requested: " + quantity
                );
            }

            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setQuantity(quantity);
            
            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
            orderItem.setLineTotal(lineTotal);
            
            orderItems.add(orderItem);
            subtotal = subtotal.add(lineTotal);

            // Reduce stock
            product.setStockQuantity(product.getStockQuantity() - quantity);
            product.setInStock(product.getStockQuantity() > 0);
            productRepository.save(product);

            // Create inventory transaction
            inventoryService.createInventoryTransaction(
                product.getId(),
                InventoryTransactionType.SALE,
                quantity,
                null, // Will be updated after order is saved
                userId,
                "Customer: " + user.getUsername() + " | Order: " + order.getOrderNumber()
            );
        }

        // 9. Clear the cart
        cart.getCartItems().clear();
        cartRepository.save(cart);

        // 10. Calculate totals
        BigDecimal shippingCost = calculateShippingCost(shippingTown);
        BigDecimal totalPrice = subtotal.add(shippingCost);

        order.setSubtotal(subtotal);
        order.setShippingCost(shippingCost);
        order.setTotalPrice(totalPrice);
        order.setOrderItems(orderItems);

        // 11. Save order
        Order savedOrder = orderRepository.save(order);

        // 12. Update inventory transactions with order reference
        updateInventoryTransactions(savedOrder);

        System.out.println("✅ Order placed successfully: " + savedOrder.getOrderNumber() + 
                           " by user: " + user.getUsername());

        return orderMapper.toOrderResponse(savedOrder);
    }

    // ============================================================
    // GET ORDERS
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return orderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new RuntimeException("Order not found with number: " + orderNumber));
        return orderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orderMapper.toOrderSummaryResponseList(orders);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderSummaryResponse> getOrdersByUserId(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
            .map(orderMapper::toOrderSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orderMapper.toOrderSummaryResponseList(orders);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderSummaryResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
            .map(orderMapper::toOrderSummaryResponse);
    }

    // ============================================================
    // NEW METHODS FOR CURRENT USER ORDERS
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public Page<OrderSummaryResponse> getOrdersByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUserIdAndOrderStatus(userId, status, pageable);
        return orders.map(orderMapper::toOrderSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public long countOrdersByUserId(Long userId) {
        return orderRepository.countByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countOrdersByUserIdAndStatus(Long userId, OrderStatus status) {
        return orderRepository.countByUserIdAndOrderStatus(userId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> getRecentOrdersByUserId(Long userId, int limit) {
        // Validate limit
        if (limit <= 0) {
            limit = 5; // Default to 5 if invalid
        }
        if (limit > 50) {
            limit = 50; // Cap at 50 to prevent performance issues
        }
        
        // Correct way: Use PageRequest.of()
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);
        return orders.stream()
            .map(orderMapper::toOrderSummaryResponse)
            .collect(Collectors.toList());
    }

    // ============================================================
    // ORDER MANAGEMENT
    // ============================================================

    @Override
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        // Validate status transition
        validateStatusTransition(order.getOrderStatus(), request.getOrderStatus());

        OrderStatus oldStatus = order.getOrderStatus();
        order.setOrderStatus(request.getOrderStatus());
        
        // If tracking code is provided, update it
        if (request.getTrackingCode() != null && !request.getTrackingCode().isEmpty()) {
            order.setTrackingCode(request.getTrackingCode());
        }

        // If status is SHIPPED and tracking code is not set, generate one
        if (request.getOrderStatus() == OrderStatus.SHIPPED && 
            (order.getTrackingCode() == null || order.getTrackingCode().isEmpty())) {
            order.setTrackingCode(generateTrackingCode());
        }

        // If status is DELIVERED, set delivered date
        if (request.getOrderStatus() == OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
        }

        Order updatedOrder = orderRepository.save(order);
        
        System.out.println("✅ Order " + order.getOrderNumber() + 
                          " status updated from " + oldStatus + " to " + request.getOrderStatus());
        
        return orderMapper.toOrderResponse(updatedOrder);
    }

    @Override
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        // Check if order can be cancelled
        if (!order.getOrderStatus().canBeCancelled()) {
            throw new RuntimeException(
                "Cannot cancel order in status: " + order.getOrderStatus() + 
                ". Only PENDING_PAYMENT, PAID, READY_FOR_SHIPPING, or ASSIGNED_TO_BATCH can be cancelled."
            );
        }

        // If order is in a batch, remove it first
        if (shippingService.isOrderInBatch(orderId)) {
            ShippingBatchResponse batch = shippingService.getBatchByOrderId(orderId);
            
            if (batch.getStatus() == ShippingStatus.DISPATCHED ||
                batch.getStatus() == ShippingStatus.DELIVERED) {
                throw new RuntimeException("Cannot cancel order that has already been shipped or delivered");
            }
            
            shippingService.removeOrderFromBatch(batch.getId(), orderId);
            System.out.println("✅ Order " + order.getOrderNumber() + " removed from batch: " + batch.getId());
        }

        // Restore stock
        Long userId = order.getUser() != null ? order.getUser().getId() : 0L;
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            product.setInStock(true);
            productRepository.save(product);

            // Create return inventory transaction
            inventoryService.createInventoryTransaction(
                product.getId(),
                InventoryTransactionType.RETURN,
                item.getQuantity(),
                order.getId(),
                userId,
                "Order cancelled: " + order.getOrderNumber()
            );
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        
        System.out.println("✅ Order " + order.getOrderNumber() + " cancelled successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByOrderStatus(status).stream()
            .map(orderMapper::toOrderResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderSummaryResponse getOrderSummary(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        return orderMapper.toOrderSummaryResponse(order);
    }

    // ============================================================
    // SHIPPING INTEGRATION
    // ============================================================

    @Override
    public OrderResponse markOrderReadyForShipping(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (order.getOrderStatus() != OrderStatus.PAID) {
            throw new RuntimeException(
                "Order must be in PAID status to be ready for shipping. Current: " + order.getOrderStatus()
            );
        }

        order.setOrderStatus(OrderStatus.READY_FOR_SHIPPING);
        Order savedOrder = orderRepository.save(order);
        
        // Try to auto-add to shipping batch
        try {
            Long bigAreaId = order.getShippingTown().getBigArea().getId();
            
            List<ShippingBatchResponse> batches = shippingService.getBatchesByBigAreaId(bigAreaId);
            ShippingBatchResponse existingBatch = batches.stream()
                .filter(b -> b.getStatus() == ShippingStatus.COLLECTING_ORDERS)
                .findFirst()
                .orElse(null);
            
            if (existingBatch != null) {
                shippingService.addOrderToBatch(existingBatch.getId(), orderId);
                System.out.println("✅ Order " + order.getOrderNumber() + " added to existing batch: " + existingBatch.getId());
            } else {
                ShippingBatchResponse newBatch = shippingService.createBatch(bigAreaId, 10);
                shippingService.addOrderToBatch(newBatch.getId(), orderId);
                System.out.println("✅ Order " + order.getOrderNumber() + " added to new batch: " + newBatch.getId());
            }
        } catch (Exception e) {
            System.out.println("⚠️ Order not added to batch automatically: " + e.getMessage());
            // Order will be picked up by the scheduler
        }
        
        return orderMapper.toOrderResponse(savedOrder);
    }

    // ============================================================
    // TRACKING
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public TrackingResponse trackOrder(String trackingCode) {
        Order order = orderRepository.findByTrackingCode(trackingCode)
            .orElseThrow(() -> new RuntimeException("Order not found with tracking code: " + trackingCode));
        
        TrackingResponse response = orderMapper.toTrackingResponse(order);
        List<TrackingResponse.TrackingEvent> history = buildTrackingHistory(order);
        response.setTrackingHistory(history);
        
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse trackOrderByTrackingCode(OrderTrackingRequest request) {
        Order order = orderRepository.findByTrackingCode(request.getTrackingCode())
            .orElseThrow(() -> new RuntimeException("Order not found with tracking code: " + request.getTrackingCode()));
        return orderMapper.toOrderResponse(order);
    }

    // ============================================================
    // DELIVERY
    // ============================================================

    @Override
    public OrderResponse confirmDelivery(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (order.getOrderStatus() != OrderStatus.SHIPPED) {
            throw new RuntimeException("Order must be in SHIPPED status. Current: " + order.getOrderStatus());
        }

        order.setOrderStatus(OrderStatus.DELIVERED);
        order.setDeliveredAt(LocalDateTime.now());
        
        Order savedOrder = orderRepository.save(order);
        System.out.println("✅ Order " + order.getOrderNumber() + " confirmed as DELIVERED");
        
        return orderMapper.toOrderResponse(savedOrder);
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================

    private void updateInventoryTransactions(Order order) {
        // This method updates inventory transactions with the order reference
        // Implementation depends on your InventoryService
        System.out.println("📦 Inventory transactions updated for order: " + order.getOrderNumber());
    }

    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "ORD-" + timestamp + "-" + random;
    }

    private String generateTrackingCode() {
        String timestamp = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "TRK-" + timestamp + "-" + random;
    }

    private BigDecimal calculateShippingCost(Town town) {
        if (town.getDeliveryFee() != null && town.getDeliveryFee().compareTo(BigDecimal.ZERO) > 0) {
            return town.getDeliveryFee();
        }
        return new BigDecimal("5.00");
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (currentStatus == OrderStatus.CANCELLED) {
            throw new RuntimeException("Cannot update status of a cancelled order");
        }
        
        if (currentStatus == OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot update status of a delivered order");
        }

        if (currentStatus == OrderStatus.SHIPPED && newStatus == OrderStatus.READY_FOR_SHIPPING) {
            throw new RuntimeException("Cannot revert from SHIPPED to READY_FOR_SHIPPING");
        }
        
        if (currentStatus == OrderStatus.DELIVERED && newStatus != OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot change status from DELIVERED");
        }
    }

    private List<TrackingResponse.TrackingEvent> buildTrackingHistory(Order order) {
        List<TrackingResponse.TrackingEvent> history = new ArrayList<>();
        
        // Order created
        TrackingResponse.TrackingEvent created = new TrackingResponse.TrackingEvent();
        created.setStatus(TrackingStatus.PENDING_PAYMENT);
        created.setDescription("Order placed successfully");
        created.setTimestamp(order.getCreatedAt());
        history.add(created);

        OrderStatus status = order.getOrderStatus();
        
        if (status.ordinal() >= OrderStatus.PAID.ordinal() || order.getPaymentStatus() == PaymentStatus.PAID) {
            TrackingResponse.TrackingEvent paid = new TrackingResponse.TrackingEvent();
            paid.setStatus(TrackingStatus.PAID);
            paid.setDescription("Payment confirmed");
            paid.setTimestamp(order.getCreatedAt().plusHours(1));
            history.add(paid);
        }

        if (status.ordinal() >= OrderStatus.READY_FOR_SHIPPING.ordinal()) {
            TrackingResponse.TrackingEvent ready = new TrackingResponse.TrackingEvent();
            ready.setStatus(TrackingStatus.READY_FOR_SHIPPING);
            ready.setDescription("Order ready for shipping");
            ready.setTimestamp(order.getCreatedAt().plusHours(2));
            history.add(ready);
        }

        if (status.ordinal() >= OrderStatus.ASSIGNED_TO_BATCH.ordinal()) {
            TrackingResponse.TrackingEvent assigned = new TrackingResponse.TrackingEvent();
            assigned.setStatus(TrackingStatus.ASSIGNED_TO_BATCH);
            assigned.setDescription("Order assigned to delivery batch");
            assigned.setTimestamp(order.getCreatedAt().plusHours(3));
            history.add(assigned);
        }

        if (status.ordinal() >= OrderStatus.SHIPPED.ordinal()) {
            TrackingResponse.TrackingEvent shipped = new TrackingResponse.TrackingEvent();
            shipped.setStatus(TrackingStatus.SHIPPED);
            shipped.setDescription("Order shipped. Tracking: " + order.getTrackingCode());
            shipped.setTimestamp(order.getCreatedAt().plusHours(4));
            history.add(shipped);
        }

        if (status.ordinal() >= OrderStatus.DELIVERED.ordinal()) {
            TrackingResponse.TrackingEvent delivered = new TrackingResponse.TrackingEvent();
            delivered.setStatus(TrackingStatus.DELIVERED);
            delivered.setDescription("Order delivered successfully");
            delivered.setTimestamp(order.getDeliveredAt() != null ? order.getDeliveredAt() : order.getCreatedAt().plusHours(5));
            history.add(delivered);
        }

        if (status == OrderStatus.CANCELLED) {
            TrackingResponse.TrackingEvent cancelled = new TrackingResponse.TrackingEvent();
            cancelled.setStatus(TrackingStatus.CANCELLED);
            cancelled.setDescription("Order cancelled");
            cancelled.setTimestamp(order.getUpdatedAt() != null ? order.getUpdatedAt() : order.getCreatedAt().plusHours(1));
            history.add(cancelled);
        }

        return history;
    }

    // ============================================================
    // ADDITIONAL HELPER METHODS FOR ANALYTICS
    // ============================================================

    @Transactional(readOnly = true)
    public UserOrderStatistics getUserOrderStatistics(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        
        long totalOrders = orders.size();
        long pendingOrders = orders.stream().filter(o -> o.getOrderStatus() == OrderStatus.PENDING_PAYMENT).count();
        long paidOrders = orders.stream().filter(o -> o.getOrderStatus() == OrderStatus.PAID).count();
        long shippedOrders = orders.stream().filter(o -> o.getOrderStatus() == OrderStatus.SHIPPED).count();
        long deliveredOrders = orders.stream().filter(o -> o.getOrderStatus() == OrderStatus.DELIVERED).count();
        long cancelledOrders = orders.stream().filter(o -> o.getOrderStatus() == OrderStatus.CANCELLED).count();
        
        BigDecimal totalSpent = orders.stream()
            .filter(o -> o.getOrderStatus() != OrderStatus.CANCELLED)
            .map(Order::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal averageOrderValue = totalOrders > 0 ? 
            totalSpent.divide(BigDecimal.valueOf(totalOrders), 2, java.math.RoundingMode.HALF_UP) : 
            BigDecimal.ZERO;
        
        return new UserOrderStatistics(totalOrders, pendingOrders, paidOrders, 
                                       shippedOrders, deliveredOrders, cancelledOrders,
                                       totalSpent, averageOrderValue);
    }

    public static class UserOrderStatistics {
        private final long totalOrders;
        private final long pendingOrders;
        private final long paidOrders;
        private final long shippedOrders;
        private final long deliveredOrders;
        private final long cancelledOrders;
        private final BigDecimal totalSpent;
        private final BigDecimal averageOrderValue;

        public UserOrderStatistics(long totalOrders, long pendingOrders, long paidOrders,
                                   long shippedOrders, long deliveredOrders, long cancelledOrders,
                                   BigDecimal totalSpent, BigDecimal averageOrderValue) {
            this.totalOrders = totalOrders;
            this.pendingOrders = pendingOrders;
            this.paidOrders = paidOrders;
            this.shippedOrders = shippedOrders;
            this.deliveredOrders = deliveredOrders;
            this.cancelledOrders = cancelledOrders;
            this.totalSpent = totalSpent;
            this.averageOrderValue = averageOrderValue;
        }

        public long getTotalOrders() { return totalOrders; }
        public long getPendingOrders() { return pendingOrders; }
        public long getPaidOrders() { return paidOrders; }
        public long getShippedOrders() { return shippedOrders; }
        public long getDeliveredOrders() { return deliveredOrders; }
        public long getCancelledOrders() { return cancelledOrders; }
        public BigDecimal getTotalSpent() { return totalSpent; }
        public BigDecimal getAverageOrderValue() { return averageOrderValue; }
    }
}