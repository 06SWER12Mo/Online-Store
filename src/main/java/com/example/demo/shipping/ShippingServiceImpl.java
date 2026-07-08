package com.example.demo.shipping;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.location.BigArea;
import com.example.demo.location.BigAreaRepository;
import com.example.demo.order.Order;
import com.example.demo.order.OrderRepository;
import com.example.demo.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ShippingServiceImpl implements ShippingService {

    private final ShippingBatchRepository shippingBatchRepository;
    private final ShippingBatchOrderRepository shippingBatchOrderRepository;
    private final BusRepository busRepository;
    private final OrderRepository orderRepository;
    private final BigAreaRepository bigAreaRepository;
    private final ShippingMapper shippingMapper;

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

    @Override
    public ShippingBatchResponse createBatch(Long bigAreaId, Integer minimumOrders) {
        BigArea bigArea = bigAreaRepository.findById(bigAreaId)
            .orElseThrow(() -> new RuntimeException("BigArea not found"));

        ShippingBatch batch = new ShippingBatch();
        batch.setBigArea(bigArea);
        batch.setMinimumOrders(minimumOrders != null ? minimumOrders : 10);
        batch.setStatus(ShippingStatus.CollectingOrders);

        ShippingBatch savedBatch = shippingBatchRepository.save(batch);
        return shippingMapper.toShippingBatchResponse(savedBatch);
    }

    @Override
    @Transactional(readOnly = true)
    public ShippingBatchResponse getBatchById(Long id) {
        ShippingBatch batch = shippingBatchRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Shipping batch not found"));
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

    @Override
    public ShippingBatchResponse addOrderToBatch(Long batchId, Long orderId) {
        ShippingBatch batch = shippingBatchRepository.findById(batchId)
            .orElseThrow(() -> new RuntimeException("Shipping batch not found"));

        if (batch.getStatus() != ShippingStatus.CollectingOrders) {
            throw new RuntimeException("Cannot add orders to batch that is not in CollectingOrders status");
        }

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getOrderStatus() != OrderStatus.ReadyForShipping) {
            throw new RuntimeException("Order must be in ReadyForShipping status to be added to batch");
        }

        // Check if order is already in a batch
        if (shippingBatchOrderRepository.existsByOrderId(orderId)) {
            throw new RuntimeException("Order is already assigned to a batch");
        }

        ShippingBatchOrder batchOrder = new ShippingBatchOrder(batch, order);
        batch.addOrder(batchOrder);

        order.setOrderStatus(OrderStatus.AssignedToBatch);
        orderRepository.save(order);

        ShippingBatch savedBatch = shippingBatchRepository.save(batch);
        return shippingMapper.toShippingBatchResponse(savedBatch);
    }

    @Override
    public ShippingBatchResponse removeOrderFromBatch(Long batchId, Long orderId) {
        ShippingBatch batch = shippingBatchRepository.findById(batchId)
            .orElseThrow(() -> new RuntimeException("Shipping batch not found"));

        if (batch.getStatus() != ShippingStatus.CollectingOrders) {
            throw new RuntimeException("Cannot remove orders from batch that is not in CollectingOrders status");
        }

        ShippingBatchOrder batchOrder = shippingBatchOrderRepository
            .findByBatchIdAndOrderId(batchId, orderId)
            .orElseThrow(() -> new RuntimeException("Order not found in batch"));

        batch.removeOrder(batchOrder);
        shippingBatchOrderRepository.delete(batchOrder);

        Order order = batchOrder.getOrder();
        order.setOrderStatus(OrderStatus.ReadyForShipping);
        orderRepository.save(order);

        ShippingBatch savedBatch = shippingBatchRepository.save(batch);
        return shippingMapper.toShippingBatchResponse(savedBatch);
    }

    @Override
    public ShippingBatchResponse assignBusToBatch(AssignBusRequest request) {
        ShippingBatch batch = shippingBatchRepository.findById(request.getBatchId())
            .orElseThrow(() -> new RuntimeException("Shipping batch not found"));

        Bus bus = busRepository.findById(request.getBusId())
            .orElseThrow(() -> new RuntimeException("Bus not found"));

        if (!bus.getIsActive()) {
            throw new RuntimeException("Cannot assign inactive bus to batch");
        }

        if (batch.getStatus() != ShippingStatus.ReadyToDispatch) {
            throw new RuntimeException("Bus can only be assigned to batch in ReadyToDispatch status");
        }

        batch.setBus(bus);
        ShippingBatch savedBatch = shippingBatchRepository.save(batch);
        return shippingMapper.toShippingBatchResponse(savedBatch);
    }

    @Override
    public ShippingBatchResponse markBatchReadyToDispatch(Long batchId) {
        ShippingBatch batch = shippingBatchRepository.findById(batchId)
            .orElseThrow(() -> new RuntimeException("Shipping batch not found"));

        if (batch.getStatus() != ShippingStatus.CollectingOrders) {
            throw new RuntimeException("Batch must be in CollectingOrders status");
        }

        if (!batch.isReadyToDispatch()) {
            throw new RuntimeException("Batch does not have enough orders. Minimum required: " + batch.getMinimumOrders());
        }

        batch.setStatus(ShippingStatus.ReadyToDispatch);
        ShippingBatch savedBatch = shippingBatchRepository.save(batch);
        return shippingMapper.toShippingBatchResponse(savedBatch);
    }

    @Override
    public ShippingBatchResponse dispatchBatch(Long batchId) {
        ShippingBatch batch = shippingBatchRepository.findById(batchId)
            .orElseThrow(() -> new RuntimeException("Shipping batch not found"));

        if (batch.getStatus() != ShippingStatus.ReadyToDispatch) {
            throw new RuntimeException("Batch must be in ReadyToDispatch status");
        }

        if (batch.getBus() == null) {
            throw new RuntimeException("No bus assigned to batch");
        }

        batch.setStatus(ShippingStatus.Dispatched);
        batch.setDispatchedAt(LocalDateTime.now());

        // Update all orders to Shipped status
        for (ShippingBatchOrder sbo : batch.getShippingBatchOrders()) {
            Order order = sbo.getOrder();
            order.setOrderStatus(OrderStatus.Shipped);
            orderRepository.save(order);
        }

        ShippingBatch savedBatch = shippingBatchRepository.save(batch);
        return shippingMapper.toShippingBatchResponse(savedBatch);
    }

    @Override
    public ShippingBatchResponse confirmDelivery(DeliveryConfirmationRequest request) {
        ShippingBatch batch = shippingBatchRepository.findById(request.getBatchId())
            .orElseThrow(() -> new RuntimeException("Shipping batch not found"));

        if (batch.getStatus() != ShippingStatus.Dispatched) {
            throw new RuntimeException("Batch must be in Dispatched status");
        }

        batch.setStatus(ShippingStatus.Delivered);
        batch.setDeliveredAt(LocalDateTime.now());

        // Update all orders to Delivered status
        for (ShippingBatchOrder sbo : batch.getShippingBatchOrders()) {
            Order order = sbo.getOrder();
            order.setOrderStatus(OrderStatus.Delivered);
            orderRepository.save(order);
        }

        ShippingBatch savedBatch = shippingBatchRepository.save(batch);
        return shippingMapper.toShippingBatchResponse(savedBatch);
    }

    @Override
    public ShippingBatchResponse cancelBatch(Long batchId) {
        ShippingBatch batch = shippingBatchRepository.findById(batchId)
            .orElseThrow(() -> new RuntimeException("Shipping batch not found"));

        if (batch.getStatus() == ShippingStatus.Delivered) {
            throw new RuntimeException("Cannot cancel already delivered batch");
        }

        // Return orders to ReadyForShipping status
        for (ShippingBatchOrder sbo : batch.getShippingBatchOrders()) {
            Order order = sbo.getOrder();
            if (order.getOrderStatus() == OrderStatus.AssignedToBatch ||
                order.getOrderStatus() == OrderStatus.Shipped) {
                order.setOrderStatus(OrderStatus.ReadyForShipping);
                orderRepository.save(order);
            }
        }

        batch.setStatus(ShippingStatus.Cancelled);
        ShippingBatch savedBatch = shippingBatchRepository.save(batch);
        return shippingMapper.toShippingBatchResponse(savedBatch);
    }

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
}