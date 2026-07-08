package com.example.demo.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.product.Product;
import com.example.demo.product.ProductRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import com.example.demo.location.Town;
import com.example.demo.location.TownRepository;
import com.example.demo.inventory.InventoryTransaction;
import com.example.demo.inventory.InventoryTransactionRepository;
import com.example.demo.inventory.InventoryTransactionType;
import com.example.demo.inventory.InventoryReferenceType;
import com.example.demo.payment.PaymentStatus;

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
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final TownRepository townRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            TownRepository townRepository,
            InventoryTransactionRepository inventoryTransactionRepository,
            OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.townRepository = townRepository;
        this.inventoryTransactionRepository = inventoryTransactionRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public OrderResponse placeOrder(PlaceOrderRequest request) {
        // Fetch user (optional - can be guest order)
        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        }

        // Fetch shipping town
        Town shippingTown = townRepository.findById(request.getShippingTownId())
            .orElseThrow(() -> new RuntimeException("Town not found"));

        // Create order
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUser(user);
        order.setGuestName(request.getGuestName());
        order.setGuestEmail(request.getGuestEmail());
        order.setGuestPhone(request.getGuestPhone());
        order.setShippingName(request.getShippingName());
        order.setShippingPhone(request.getShippingPhone());
        order.setShippingTown(shippingTown);
        order.setShippingStreet(request.getShippingStreet());
        order.setShippingBuilding(request.getShippingBuilding());
        order.setLatitude(request.getLatitude());
        order.setLongitude(request.getLongitude());
        order.setPaymentStatus(PaymentStatus.Pending);
        order.setOrderStatus(OrderStatus.PendingPayment);

        // Calculate totals and create order items
        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (PlaceOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + itemRequest.getProductId()));

            // Check stock
            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            OrderItem orderItem = orderMapper.toOrderItem(itemRequest, product, order);
            orderItems.add(orderItem);
            subtotal = subtotal.add(orderItem.getLineTotal());

            // Reduce stock
            product.setStockQuantity(product.getStockQuantity() - itemRequest.getQuantity());
            productRepository.save(product);

            // Create inventory transaction
            InventoryTransaction transaction = new InventoryTransaction();
            transaction.setProduct(product);
            transaction.setTransactionType(InventoryTransactionType.Sale);
            transaction.setQuantity(itemRequest.getQuantity());
            transaction.setReferenceType(InventoryReferenceType.Order);
            transaction.setReferenceId(null); // Will be set after order is saved
            transaction.setCreatedAt(LocalDateTime.now());
            inventoryTransactionRepository.save(transaction);
        }

        // Calculate shipping cost (simplified - could be based on location/weight)
        BigDecimal shippingCost = calculateShippingCost(shippingTown);
        BigDecimal totalPrice = subtotal.add(shippingCost);

        order.setSubtotal(subtotal);
        order.setShippingCost(shippingCost);
        order.setTotalPrice(totalPrice);
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        // Update reference IDs for inventory transactions
        List<InventoryTransaction> transactions = inventoryTransactionRepository
            .findByReferenceTypeAndReferenceId(InventoryReferenceType.Order, null);
        for (InventoryTransaction transaction : transactions) {
            transaction.setReferenceId(savedOrder.getId());
            inventoryTransactionRepository.save(transaction);
        }

        return orderMapper.toOrderResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        return orderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new RuntimeException("Order not found"));
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

    @Override
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setOrderStatus(request.getOrderStatus());
        
        if (request.getTrackingCode() != null && !request.getTrackingCode().isEmpty()) {
            order.setTrackingCode(request.getTrackingCode());
        }

        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toOrderResponse(updatedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public TrackingResponse trackOrder(String trackingCode) {
        Order order = orderRepository.findByTrackingCode(trackingCode)
            .orElseThrow(() -> new RuntimeException("Order not found with tracking code: " + trackingCode));
        
        TrackingResponse response = orderMapper.toTrackingResponse(order);
        
        // Build tracking history based on order status
        List<TrackingResponse.TrackingEvent> history = buildTrackingHistory(order);
        response.setTrackingHistory(history);
        
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse trackOrderByTrackingCode(OrderTrackingRequest request) {
        Order order = orderRepository.findByTrackingCode(request.getTrackingCode())
            .orElseThrow(() -> new RuntimeException("Order not found"));
        return orderMapper.toOrderResponse(order);
    }

    @Override
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getOrderStatus() == OrderStatus.Shipped || 
            order.getOrderStatus() == OrderStatus.Delivered) {
            throw new RuntimeException("Cannot cancel order that has already been shipped or delivered");
        }

        // Restore stock
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);

            // Create return inventory transaction
            InventoryTransaction transaction = new InventoryTransaction();
            transaction.setProduct(product);
            transaction.setTransactionType(InventoryTransactionType.Return);
            transaction.setQuantity(item.getQuantity());
            transaction.setReferenceType(InventoryReferenceType.Order);
            transaction.setReferenceId(order.getId());
            transaction.setCreatedAt(LocalDateTime.now());
            inventoryTransactionRepository.save(transaction);
        }

        order.setOrderStatus(OrderStatus.Cancelled);
        orderRepository.save(order);
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
            .orElseThrow(() -> new RuntimeException("Order not found"));
        return orderMapper.toOrderSummaryResponse(order);
    }

    // Helper methods
    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private BigDecimal calculateShippingCost(Town town) {
        // Simplified shipping cost calculation
        // In a real app, this would consider distance, weight, etc.
        return new BigDecimal("5.00");
    }

    private List<TrackingResponse.TrackingEvent> buildTrackingHistory(Order order) {
        List<TrackingResponse.TrackingEvent> history = new ArrayList<>();
        
        // Order created
        TrackingResponse.TrackingEvent created = new TrackingResponse.TrackingEvent();
        created.setStatus(TrackingStatus.PendingPayment);
        created.setDescription("Order placed successfully");
        created.setTimestamp(order.getCreatedAt());
        history.add(created);

        // Add events based on current status
        OrderStatus status = order.getOrderStatus();
        
        if (status.ordinal() >= OrderStatus.Paid.ordinal()) {
            TrackingResponse.TrackingEvent paid = new TrackingResponse.TrackingEvent();
            paid.setStatus(TrackingStatus.Paid);
            paid.setDescription("Payment confirmed");
            paid.setTimestamp(order.getCreatedAt().plusHours(1)); // Placeholder
            history.add(paid);
        }

        if (status.ordinal() >= OrderStatus.ReadyForShipping.ordinal()) {
            TrackingResponse.TrackingEvent ready = new TrackingResponse.TrackingEvent();
            ready.setStatus(TrackingStatus.ReadyForShipping);
            ready.setDescription("Order prepared for shipping");
            ready.setTimestamp(order.getCreatedAt().plusHours(2));
            history.add(ready);
        }

        if (status.ordinal() >= OrderStatus.AssignedToBatch.ordinal()) {
            TrackingResponse.TrackingEvent assigned = new TrackingResponse.TrackingEvent();
            assigned.setStatus(TrackingStatus.AssignedToBatch);
            assigned.setDescription("Order assigned to delivery batch");
            assigned.setTimestamp(order.getCreatedAt().plusHours(3));
            history.add(assigned);
        }

        if (status.ordinal() >= OrderStatus.Shipped.ordinal()) {
            TrackingResponse.TrackingEvent shipped = new TrackingResponse.TrackingEvent();
            shipped.setStatus(TrackingStatus.Shipped);
            shipped.setDescription("Order shipped. Tracking: " + order.getTrackingCode());
            shipped.setTimestamp(order.getCreatedAt().plusHours(4));
            history.add(shipped);
        }

        if (status.ordinal() >= OrderStatus.Delivered.ordinal()) {
            TrackingResponse.TrackingEvent delivered = new TrackingResponse.TrackingEvent();
            delivered.setStatus(TrackingStatus.Delivered);
            delivered.setDescription("Order delivered successfully");
            delivered.setTimestamp(order.getCreatedAt().plusHours(5));
            history.add(delivered);
        }

        if (status == OrderStatus.Cancelled) {
            TrackingResponse.TrackingEvent cancelled = new TrackingResponse.TrackingEvent();
            cancelled.setStatus(TrackingStatus.Cancelled);
            cancelled.setDescription("Order cancelled");
            cancelled.setTimestamp(order.getCreatedAt().plusHours(1));
            history.add(cancelled);
        }

        return history;
    }
}