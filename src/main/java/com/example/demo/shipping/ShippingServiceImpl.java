package com.example.demo.shipping;

import com.example.demo.location.BigArea;
import com.example.demo.location.BigAreaRepository;
import com.example.demo.order.Order;
import com.example.demo.order.OrderRepository;
import com.example.demo.order.OrderStatus;
import com.example.demo.shipping.dtos.AssignBusRequest;
import com.example.demo.shipping.dtos.DeliveryConfirmationRequest;
import com.example.demo.shipping.dtos.ShippingBatchResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ShippingServiceImpl implements ShippingService {

    private final ShippingBatchRepository shippingBatchRepository;
    private final ShippingBatchOrderRepository shippingBatchOrderRepository;
    private final BusRepository busRepository;
    private final OrderRepository orderRepository;
    private final BigAreaRepository bigAreaRepository;
    private final ShippingMapper shippingMapper;

    @Value("${shipping.auto-deliver-hours:5}")
    private int autoDeliverHours;

    public ShippingServiceImpl(
            ShippingBatchRepository shippingBatchRepository,
            ShippingBatchOrderRepository shippingBatchOrderRepository,
            BusRepository busRepository,
            OrderRepository orderRepository,
            BigAreaRepository bigAreaRepository,
            ShippingMapper shippingMapper) {
        this.shippingBatchRepository = shippingBatchRepository;
        this.shippingBatchOrderRepository = shippingBatchOrderRepository;
        this.busRepository = busRepository;
        this.orderRepository = orderRepository;
        this.bigAreaRepository = bigAreaRepository;
        this.shippingMapper = shippingMapper;
    }

    // ========== BATCH OPERATIONS ==========

    @Override
    public ShippingBatchResponse createBatch(Long bigAreaId, Integer minimumOrders) {
        BigArea bigArea = bigAreaRepository.findById(bigAreaId)
            .orElseThrow(() -> new RuntimeException("BigArea not found with id: " + bigAreaId));

        ShippingBatch batch = new ShippingBatch();
        batch.setBigArea(bigArea);
        batch.setMinimumOrders(minimumOrders != null ? minimumOrders : 10);
        batch.setStatus(ShippingStatus.COLLECTING_ORDERS);

        ShippingBatch savedBatch = shippingBatchRepository.save(batch);
        return shippingMapper.toShippingBatchResponse(savedBatch);
    }

    @Override
    @Transactional(readOnly = true)
    public ShippingBatchResponse getBatchById(Long id) {
        ShippingBatch batch = shippingBatchRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Shipping batch not found with id: " + id));
        return shippingMapper.toShippingBatchResponse(batch);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShippingBatchResponse> getAllBatches() {
        return shippingMapper.toShippingBatchResponseList(shippingBatchRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShippingBatchResponse> getBatchesByStatus(ShippingStatus status) {
        return shippingMapper.toShippingBatchResponseList(
            shippingBatchRepository.findByStatus(status));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShippingBatchResponse> getBatchesByBigAreaId(Long bigAreaId) {
        return shippingMapper.toShippingBatchResponseList(
            shippingBatchRepository.findByBigAreaId(bigAreaId));
    }

    // ========== ORDER ASSIGNMENT ==========

    @Override
    public ShippingBatchResponse addOrderToBatch(Long batchId, Long orderId) {
        ShippingBatch batch = shippingBatchRepository.findById(batchId)
            .orElseThrow(() -> new RuntimeException("Shipping batch not found with id: " + batchId));

        if (batch.getStatus() != ShippingStatus.COLLECTING_ORDERS) {
            throw new RuntimeException("Batch is not collecting orders. Current: " + batch.getStatus());
        }

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (order.getOrderStatus() != OrderStatus.READY_FOR_SHIPPING) {
            throw new RuntimeException("Order must be in READY_FOR_SHIPPING. Current: " + order.getOrderStatus());
        }

        if (shippingBatchOrderRepository.existsByOrderId(orderId)) {
            throw new RuntimeException("Order is already assigned to a batch");
        }

        ShippingBatchOrder batchOrder = new ShippingBatchOrder(batch, order);
        batch.addOrder(batchOrder);

        order.setOrderStatus(OrderStatus.ASSIGNED_TO_BATCH);
        orderRepository.save(order);

        ShippingBatch savedBatch = shippingBatchRepository.save(batch);
        return shippingMapper.toShippingBatchResponse(savedBatch);
    }

    @Override
    public ShippingBatchResponse removeOrderFromBatch(Long batchId, Long orderId) {
        ShippingBatch batch = shippingBatchRepository.findById(batchId)
            .orElseThrow(() -> new RuntimeException("Shipping batch not found with id: " + batchId));

        if (batch.getStatus() != ShippingStatus.COLLECTING_ORDERS) {
            throw new RuntimeException("Cannot remove orders from batch in status: " + batch.getStatus());
        }

        ShippingBatchOrder batchOrder = shippingBatchOrderRepository
            .findByBatchIdAndOrderId(batchId, orderId)
            .orElseThrow(() -> new RuntimeException("Order not found in batch"));

        batch.removeOrder(batchOrder);
        shippingBatchOrderRepository.delete(batchOrder);

        Order order = batchOrder.getOrder();
        order.setOrderStatus(OrderStatus.READY_FOR_SHIPPING);
        orderRepository.save(order);

        ShippingBatch savedBatch = shippingBatchRepository.save(batch);
        return shippingMapper.toShippingBatchResponse(savedBatch);
    }

    // ========== BUS ASSIGNMENT - MANUAL ==========

    @Override
    public ShippingBatchResponse assignBusToBatch(AssignBusRequest request) {
        ShippingBatch batch = shippingBatchRepository.findById(request.getBatchId())
            .orElseThrow(() -> new RuntimeException("Shipping batch not found with id: " + request.getBatchId()));

        Bus bus = busRepository.findById(request.getBusId())
            .orElseThrow(() -> new RuntimeException("Bus not found with id: " + request.getBusId()));

        if (!bus.getIsActive()) {
            throw new RuntimeException("Cannot assign inactive bus");
        }

        if (batch.getStatus() != ShippingStatus.READY_TO_DISPATCH) {
            throw new RuntimeException("Bus can only be assigned to READY_TO_DISPATCH batches. Current: " + batch.getStatus());
        }

        batch.setBus(bus);
        ShippingBatch savedBatch = shippingBatchRepository.save(batch);
        return shippingMapper.toShippingBatchResponse(savedBatch);
    }

    // ========== ✅ BUS ASSIGNMENT - AUTO ==========

    @Override
    public ShippingBatchResponse autoAssignBus(Long batchId) {
        ShippingBatch batch = shippingBatchRepository.findById(batchId)
            .orElseThrow(() -> new RuntimeException("Shipping batch not found with id: " + batchId));

        if (batch.getStatus() != ShippingStatus.READY_TO_DISPATCH) {
            throw new RuntimeException("Batch must be in READY_TO_DISPATCH status. Current: " + batch.getStatus());
        }

        if (batch.getBus() != null) {
            throw new RuntimeException("Batch already has a bus assigned: " + batch.getBus().getPlateNumber());
        }

        Bus availableBus = getFirstAvailableBus();
        
        if (availableBus == null) {
            throw new RuntimeException("No available buses found!");
        }

        batch.setBus(availableBus);
        ShippingBatch savedBatch = shippingBatchRepository.save(batch);
        
        System.out.println("🚌 Auto-assigned Bus " + availableBus.getPlateNumber() + 
                           " (Driver: " + availableBus.getDriverName() + 
                           ") to Batch #" + batchId);

        return shippingMapper.toShippingBatchResponse(savedBatch);
    }

    @Override
    @Transactional(readOnly = true)
    public Bus getFirstAvailableBus() {
        List<Bus> activeBuses = busRepository.findByIsActiveTrue();
        
        List<Long> assignedBusIds = shippingBatchRepository.findAll().stream()
            .filter(b -> b.getBus() != null)
            .filter(b -> b.getStatus() != ShippingStatus.DELIVERED && 
                         b.getStatus() != ShippingStatus.CANCELLED)
            .map(b -> b.getBus().getId())
            .toList();
        
        return activeBuses.stream()
            .filter(bus -> !assignedBusIds.contains(bus.getId()))
            .findFirst()
            .orElse(null);
    }

    // ========== BATCH LIFECYCLE ==========

    @Override
    public ShippingBatchResponse markBatchReadyToDispatch(Long batchId) {
        ShippingBatch batch = shippingBatchRepository.findById(batchId)
            .orElseThrow(() -> new RuntimeException("Shipping batch not found with id: " + batchId));

        if (batch.getStatus() != ShippingStatus.COLLECTING_ORDERS) {
            throw new RuntimeException("Batch must be in COLLECTING_ORDERS status. Current: " + batch.getStatus());
        }

        if (!batch.isReadyToDispatch()) {
            throw new RuntimeException("Batch does not have enough orders. Minimum: " + batch.getMinimumOrders() + 
                                       ", Current: " + batch.getCurrentOrderCount());
        }

        batch.setStatus(ShippingStatus.READY_TO_DISPATCH);
        ShippingBatch savedBatch = shippingBatchRepository.save(batch);
        return shippingMapper.toShippingBatchResponse(savedBatch);
    }

    @Override
    public ShippingBatchResponse dispatchBatch(Long batchId) {
        ShippingBatch batch = shippingBatchRepository.findById(batchId)
            .orElseThrow(() -> new RuntimeException("Shipping batch not found with id: " + batchId));

        if (batch.getStatus() != ShippingStatus.READY_TO_DISPATCH) {
            throw new RuntimeException("Batch must be in READY_TO_DISPATCH status. Current: " + batch.getStatus());
        }

        if (batch.getBus() == null) {
            // ✅ Try to auto-assign a bus before dispatching
            try {
                autoAssignBus(batchId);
            } catch (Exception e) {
                throw new RuntimeException("No bus assigned and no available buses to auto-assign!");
            }
        }

        batch.setStatus(ShippingStatus.DISPATCHED);
        batch.setDispatchedAt(LocalDateTime.now());
        batch.setAutoDeliverAt(LocalDateTime.now().plusHours(autoDeliverHours));

        for (ShippingBatchOrder sbo : batch.getShippingBatchOrders()) {
            Order order = sbo.getOrder();
            order.setOrderStatus(OrderStatus.SHIPPED);
            order.setTrackingCode(generateTrackingCode());
            orderRepository.save(order);
        }

        ShippingBatch savedBatch = shippingBatchRepository.save(batch);
        System.out.println("🚚 Batch " + batch.getId() + " dispatched! Auto-deliver at: " + batch.getAutoDeliverAt());
        
        return shippingMapper.toShippingBatchResponse(savedBatch);
    }

    @Override
    public ShippingBatchResponse autoDeliverBatch(Long batchId) {
        ShippingBatch batch = shippingBatchRepository.findById(batchId)
            .orElseThrow(() -> new RuntimeException("Shipping batch not found with id: " + batchId));

        if (batch.getStatus() != ShippingStatus.DISPATCHED) {
            throw new RuntimeException("Only dispatched batches can be auto-delivered. Current: " + batch.getStatus());
        }

        batch.setStatus(ShippingStatus.DELIVERED);
        batch.setDeliveredAt(LocalDateTime.now());

        for (ShippingBatchOrder sbo : batch.getShippingBatchOrders()) {
            Order order = sbo.getOrder();
            order.setOrderStatus(OrderStatus.DELIVERED);
            order.setDeliveredAt(LocalDateTime.now());
            orderRepository.save(order);
        }

        ShippingBatch savedBatch = shippingBatchRepository.save(batch);
        System.out.println("✅ Batch " + batch.getId() + " auto-delivered! " + 
                           batch.getShippingBatchOrders().size() + " orders delivered.");
        
        return shippingMapper.toShippingBatchResponse(savedBatch);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShippingBatch> getBatchesToAutoDeliver() {
        return shippingBatchRepository.findBatchesToAutoDeliver(LocalDateTime.now());
    }

    @Override
    public ShippingBatchResponse confirmDelivery(DeliveryConfirmationRequest request) {
        ShippingBatch batch = shippingBatchRepository.findById(request.getBatchId())
            .orElseThrow(() -> new RuntimeException("Shipping batch not found with id: " + request.getBatchId()));

        if (batch.getStatus() != ShippingStatus.DISPATCHED) {
            throw new RuntimeException("Batch must be in DISPATCHED status. Current: " + batch.getStatus());
        }

        batch.setStatus(ShippingStatus.DELIVERED);
        batch.setDeliveredAt(LocalDateTime.now());

        for (ShippingBatchOrder sbo : batch.getShippingBatchOrders()) {
            Order order = sbo.getOrder();
            order.setOrderStatus(OrderStatus.DELIVERED);
            order.setDeliveredAt(LocalDateTime.now());
            orderRepository.save(order);
        }

        ShippingBatch savedBatch = shippingBatchRepository.save(batch);
        return shippingMapper.toShippingBatchResponse(savedBatch);
    }

    @Override
    public ShippingBatchResponse cancelBatch(Long batchId) {
        ShippingBatch batch = shippingBatchRepository.findById(batchId)
            .orElseThrow(() -> new RuntimeException("Shipping batch not found with id: " + batchId));

        if (batch.getStatus() == ShippingStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel already delivered batch");
        }

        for (ShippingBatchOrder sbo : batch.getShippingBatchOrders()) {
            Order order = sbo.getOrder();
            if (order.getOrderStatus() == OrderStatus.ASSIGNED_TO_BATCH ||
                order.getOrderStatus() == OrderStatus.SHIPPED) {
                order.setOrderStatus(OrderStatus.READY_FOR_SHIPPING);
                orderRepository.save(order);
            }
        }

        batch.setStatus(ShippingStatus.CANCELLED);
        ShippingBatch savedBatch = shippingBatchRepository.save(batch);
        return shippingMapper.toShippingBatchResponse(savedBatch);
    }

    // ========== ORDER TRACKING ==========

    @Override
    @Transactional(readOnly = true)
    public boolean isOrderInBatch(Long orderId) {
        return shippingBatchOrderRepository.existsByOrderId(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public ShippingBatchResponse getBatchByOrderId(Long orderId) {
        ShippingBatchOrder batchOrder = shippingBatchOrderRepository.findByOrderId(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found in any batch"));
        return shippingMapper.toShippingBatchResponse(batchOrder.getShippingBatch());
    }

    // ========== HELPER METHODS ==========

    private String generateTrackingCode() {
        return "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}