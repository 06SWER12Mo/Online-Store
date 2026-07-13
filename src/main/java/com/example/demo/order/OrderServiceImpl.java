package com.example.demo.order;

import com.example.demo.inventory.InventoryService;
import com.example.demo.inventory.InventoryTransactionType;
import com.example.demo.inventory.dtos.InventoryTransactionResponse;
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
import org.springframework.data.domain.Pageable;
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
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final TownRepository townRepository;
    private final OrderMapper orderMapper;
    private final InventoryService inventoryService;
    private final ShippingService shippingService;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            TownRepository townRepository,
            OrderMapper orderMapper,
            InventoryService inventoryService,
            ShippingService shippingService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.townRepository = townRepository;
        this.orderMapper = orderMapper;
        this.inventoryService = inventoryService;
        this.shippingService = shippingService;
    }

    // ========== PLACE ORDER ==========

    @Override
    public OrderResponse placeOrder(PlaceOrderRequest request) {
        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        }

        Town shippingTown = townRepository.findById(request.getShippingTownId())
            .orElseThrow(() -> new RuntimeException("Town not found"));

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
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setOrderStatus(OrderStatus.PENDING_PAYMENT);

        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        Long userId = user != null ? user.getId() : 0L;

        for (PlaceOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + itemRequest.getProductId()));

            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            OrderItem orderItem = orderMapper.toOrderItem(itemRequest, product, order);
            orderItems.add(orderItem);
            subtotal = subtotal.add(orderItem.getLineTotal());

            product.setStockQuantity(product.getStockQuantity() - itemRequest.getQuantity());
            product.setInStock(product.getStockQuantity() > 0);
            productRepository.save(product);

            inventoryService.createInventoryTransaction(
                product.getId(),
                InventoryTransactionType.SALE,
                itemRequest.getQuantity(),
                null,
                userId,
                "Customer: " + request.getGuestName() + " | Order: " + order.getOrderNumber()
            );
        }

        BigDecimal shippingCost = calculateShippingCost(shippingTown);
        BigDecimal totalPrice = subtotal.add(shippingCost);

        order.setSubtotal(subtotal);
        order.setShippingCost(shippingCost);
        order.setTotalPrice(totalPrice);
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        // Update inventory transactions with order reference
        updateInventoryTransactions(savedOrder);

        return orderMapper.toOrderResponse(savedOrder);
    }

    // ========== GET ORDERS ==========

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

    // ========== ORDER MANAGEMENT ==========

    @Override
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        order.setOrderStatus(request.getOrderStatus());
        
        if (request.getTrackingCode() != null && !request.getTrackingCode().isEmpty()) {
            order.setTrackingCode(request.getTrackingCode());
        }

        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toOrderResponse(updatedOrder);
    }

    @Override
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        // Check if order can be cancelled
        if (!order.getOrderStatus().canBeCancelled()) {
            throw new RuntimeException("Cannot cancel order that has already been shipped or delivered");
        }

        // If order is in a batch, remove it first
        if (shippingService.isOrderInBatch(orderId)) {
            ShippingBatchResponse batch = shippingService.getBatchByOrderId(orderId);
            
            if (batch.getStatus() == ShippingStatus.DISPATCHED ||
                batch.getStatus() == ShippingStatus.DELIVERED) {
                throw new RuntimeException("Cannot cancel order that has already been shipped or delivered");
            }
            
            shippingService.removeOrderFromBatch(batch.getId(), orderId);
        }

        // Restore stock
        Long userId = order.getUser() != null ? order.getUser().getId() : 0L;
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            product.setInStock(true);
            productRepository.save(product);

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

    // ========== SHIPPING INTEGRATION ==========

    @Override
    public OrderResponse markOrderReadyForShipping(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (order.getOrderStatus() != OrderStatus.PAID) {
            throw new RuntimeException("Order must be in PAID status to be ready for shipping. Current: " + order.getOrderStatus());
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
        }
        
        return orderMapper.toOrderResponse(savedOrder);
    }

    // ========== TRACKING ==========

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

    // ========== DELIVERY ==========

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
        return orderMapper.toOrderResponse(savedOrder);
    }

    // ========== HELPER METHODS ==========

    private void updateInventoryTransactions(Order order) {
        List<InventoryTransactionResponse> transactions = inventoryService.getTransactionsByReferenceId(null);
        for (InventoryTransactionResponse transaction : transactions) {
            if (transaction.getNotes() != null && 
                transaction.getNotes().contains("Order: " + order.getOrderNumber())) {
                // Update transaction with order reference
                // Implementation depends on your InventoryService
            }
        }
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private BigDecimal calculateShippingCost(Town town) {
        if (town.getDeliveryFee() != null) {
            return town.getDeliveryFee();
        }
        return new BigDecimal("5.00");
    }

    private List<TrackingResponse.TrackingEvent> buildTrackingHistory(Order order) {
        List<TrackingResponse.TrackingEvent> history = new ArrayList<>();
        
        TrackingResponse.TrackingEvent created = new TrackingResponse.TrackingEvent();
        created.setStatus(TrackingStatus.PENDING_PAYMENT);
        created.setDescription("Order placed successfully");
        created.setTimestamp(order.getCreatedAt());
        history.add(created);

        OrderStatus status = order.getOrderStatus();
        
        if (status.ordinal() >= OrderStatus.PAID.ordinal()) {
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
            delivered.setTimestamp(order.getCreatedAt().plusHours(5));
            history.add(delivered);
        }

        if (status == OrderStatus.CANCELLED) {
            TrackingResponse.TrackingEvent cancelled = new TrackingResponse.TrackingEvent();
            cancelled.setStatus(TrackingStatus.CANCELLED);
            cancelled.setDescription("Order cancelled");
            cancelled.setTimestamp(order.getCreatedAt().plusHours(1));
            history.add(cancelled);
        }

        return history;
    }
}