package com.example.demo.shipping;

import com.example.demo.order.Order;
import com.example.demo.order.OrderRepository;
import com.example.demo.shipping.dtos.BusResponse;
import com.example.demo.shipping.dtos.ShippingBatchResponse;
import com.example.demo.shipping.dtos.ShippingDashboardResponse;
import com.example.demo.shipping.dtos.ShippingStatsResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ShippingViewService {

    private final ShippingBatchRepository shippingBatchRepository;
    private final ShippingBatchOrderRepository shippingBatchOrderRepository;
    private final BusRepository busRepository;
    private final OrderRepository orderRepository;
    private final ShippingMapper shippingMapper;

    public ShippingViewService(ShippingBatchRepository shippingBatchRepository,
                               ShippingBatchOrderRepository shippingBatchOrderRepository,
                               BusRepository busRepository,
                               OrderRepository orderRepository,
                               ShippingMapper shippingMapper) {
        this.shippingBatchRepository = shippingBatchRepository;
        this.shippingBatchOrderRepository = shippingBatchOrderRepository;
        this.busRepository = busRepository;
        this.orderRepository = orderRepository;
        this.shippingMapper = shippingMapper;
    }

    // ========== BATCH VIEWING ==========

    public Page<ShippingBatchResponse> getBatches(ShippingStatus status, Long bigAreaId, 
                                                   LocalDateTime dateFrom, LocalDateTime dateTo,
                                                   Pageable pageable) {
        // You'll need to implement filtering logic in your repository
        // For now, get all and filter in memory (not ideal for large datasets)
        var allBatches = shippingBatchRepository.findAll();
        
        var filtered = allBatches.stream()
            .filter(batch -> status == null || batch.getStatus() == status)
            .filter(batch -> bigAreaId == null || 
                             (batch.getBigArea() != null && batch.getBigArea().getId().equals(bigAreaId)))
            .filter(batch -> dateFrom == null || batch.getCreatedAt().isAfter(dateFrom))
            .filter(batch -> dateTo == null || batch.getCreatedAt().isBefore(dateTo))
            .collect(Collectors.toList());
        
        // Manual pagination (you should implement repository methods for better performance)
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtered.size());
        
        List<ShippingBatchResponse> pageContent = filtered.subList(start, end).stream()
            .map(shippingMapper::toShippingBatchResponse)
            .collect(Collectors.toList());
        
        return new PageImpl<>(pageContent, pageable, filtered.size());
    }

    // ========== DASHBOARD ==========

    public ShippingDashboardResponse getDashboard() {
        ShippingDashboardResponse response = new ShippingDashboardResponse();
        
        // Count by status
        response.setTotalBatches(shippingBatchRepository.count());
        response.setCollectingOrders((long) shippingBatchRepository.findByStatus(ShippingStatus.COLLECTING_ORDERS).size());
        response.setReadyToDispatch((long) shippingBatchRepository.findByStatus(ShippingStatus.READY_TO_DISPATCH).size());
        response.setDispatched((long) shippingBatchRepository.findByStatus(ShippingStatus.DISPATCHED).size());
        response.setDelivered((long) shippingBatchRepository.findByStatus(ShippingStatus.DELIVERED).size());
        response.setCancelled((long) shippingBatchRepository.findByStatus(ShippingStatus.CANCELLED).size());
        
        // Bus stats
        var allBuses = busRepository.findAll();
        var activeBuses = busRepository.findByIsActiveTrue();
        response.setTotalBuses((long) allBuses.size());
        response.setAvailableBuses((long) getAvailableBusCount());
        response.setBusyBuses((long) (activeBuses.size() - getAvailableBusCount()));
        
        // Orders
        response.setTotalOrdersInBatches(shippingBatchOrderRepository.count());
        response.setPendingOrders(orderRepository.countByOrderStatus(com.example.demo.order.OrderStatus.READY_FOR_SHIPPING));
        
        // Recent batches (last 10)
        var recentBatches = shippingBatchRepository.findAll().stream()
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
            .limit(10)
            .map(shippingMapper::toShippingBatchResponse)
            .collect(Collectors.toList());
        response.setRecentBatches(recentBatches);
        
        // Urgent: ready to dispatch but no bus
        var urgentBatches = shippingBatchRepository.findByStatus(ShippingStatus.READY_TO_DISPATCH).stream()
            .filter(batch -> batch.getBus() == null)
            .map(shippingMapper::toShippingBatchResponse)
            .collect(Collectors.toList());
        response.setUrgentBatches(urgentBatches);
        
        // Last activity timestamps
        shippingBatchRepository.findAll().stream()
            .max((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
            .ifPresent(b -> response.setLastBatchCreated(b.getCreatedAt()));
        
        shippingBatchRepository.findByStatus(ShippingStatus.DISPATCHED).stream()
            .max((a, b) -> a.getDispatchedAt().compareTo(b.getDispatchedAt()))
            .ifPresent(b -> response.setLastBatchDispatched(b.getDispatchedAt()));
        
        shippingBatchRepository.findByStatus(ShippingStatus.DELIVERED).stream()
            .max((a, b) -> a.getDeliveredAt().compareTo(b.getDeliveredAt()))
            .ifPresent(b -> response.setLastBatchDelivered(b.getDeliveredAt()));
        
        return response;
    }

    // ========== STATISTICS ==========

    public ShippingStatsResponse getStats(LocalDateTime startDate, LocalDateTime endDate) {
        ShippingStatsResponse response = new ShippingStatsResponse();
        response.setPeriodStart(startDate);
        response.setPeriodEnd(endDate);
        
        var batchesInPeriod = shippingBatchRepository.findAll().stream()
            .filter(b -> b.getCreatedAt().isAfter(startDate) && b.getCreatedAt().isBefore(endDate))
            .collect(Collectors.toList());
        
        // Totals
        response.setTotalBatches((long) batchesInPeriod.size());
        
        long totalOrders = 0;
        long deliveredOrders = 0;
        long deliveredBatches = 0;
        double totalDispatchTime = 0;
        double totalDeliveryTime = 0;
        int dispatchedCount = 0;
        int deliveredCount = 0;
        
        for (ShippingBatch batch : batchesInPeriod) {
            totalOrders += batch.getCurrentOrderCount();
            
            if (batch.getStatus() == ShippingStatus.DELIVERED) {
                deliveredBatches++;
                deliveredOrders += batch.getCurrentOrderCount();
                if (batch.getDispatchedAt() != null) {
                    dispatchedCount++;
                    totalDispatchTime += java.time.Duration.between(batch.getCreatedAt(), batch.getDispatchedAt()).toHours();
                }
                if (batch.getDeliveredAt() != null && batch.getDispatchedAt() != null) {
                    deliveredCount++;
                    totalDeliveryTime += java.time.Duration.between(batch.getDispatchedAt(), batch.getDeliveredAt()).toHours();
                }
            }
        }
        
        response.setTotalOrders(totalOrders);
        response.setTotalBatchesDelivered(deliveredBatches);
        response.setTotalOrdersDelivered(deliveredOrders);
        
        response.setAverageOrdersPerBatch(batchesInPeriod.isEmpty() ? 0 : (double) totalOrders / batchesInPeriod.size());
        response.setAverageTimeToDispatchHours(dispatchedCount == 0 ? 0 : totalDispatchTime / dispatchedCount);
        response.setAverageDeliveryTimeHours(deliveredCount == 0 ? 0 : totalDeliveryTime / deliveredCount);
        
        // Bus utilization
        var activeBuses = busRepository.findByIsActiveTrue();
        var busyBuses = activeBuses.size() - getAvailableBusCount();
        response.setBusUtilizationRate(activeBuses.isEmpty() ? 0 : (double) busyBuses / activeBuses.size() * 100);
        
        // Daily stats
        var dailyStats = new ArrayList<ShippingStatsResponse.DailyStats>();
        LocalDateTime current = startDate;
        while (current.isBefore(endDate)) {
            LocalDateTime dayStart = current;
            LocalDateTime dayEnd = current.plusDays(1);
            
            ShippingStatsResponse.DailyStats stats = new ShippingStatsResponse.DailyStats();
            stats.setDate(dayStart);
            
            long created = batchesInPeriod.stream()
                .filter(b -> b.getCreatedAt().isAfter(dayStart) && b.getCreatedAt().isBefore(dayEnd))
                .count();
            stats.setBatchesCreated(created);
            
            long dispatched = batchesInPeriod.stream()
                .filter(b -> b.getDispatchedAt() != null && b.getDispatchedAt().isAfter(dayStart) && b.getDispatchedAt().isBefore(dayEnd))
                .count();
            stats.setBatchesDispatched(dispatched);
            
            long delivered = batchesInPeriod.stream()
                .filter(b -> b.getDeliveredAt() != null && b.getDeliveredAt().isAfter(dayStart) && b.getDeliveredAt().isBefore(dayEnd))
                .count();
            stats.setBatchesDelivered(delivered);
            
            dailyStats.add(stats);
            current = dayEnd;
        }
        
        response.setDailyStats(dailyStats);
        return response;
    }

    // ========== BUS VIEWING ==========

    public List<BusResponse> getBuses(Boolean isActive) {
        List<Bus> buses;
        if (isActive != null) {
            buses = isActive ? busRepository.findByIsActiveTrue() : busRepository.findByIsActiveFalse();
        } else {
            buses = busRepository.findAll();
        }
        
        // Get assigned batch IDs
        var assignedBusIds = shippingBatchRepository.findAll().stream()
            .filter(b -> b.getBus() != null)
            .filter(b -> b.getStatus() != ShippingStatus.DELIVERED && b.getStatus() != ShippingStatus.CANCELLED)
            .collect(Collectors.toMap(
                b -> b.getBus().getId(),
                b -> b,
                (existing, replacement) -> existing
            ));
        
        return buses.stream()
            .map(bus -> {
                BusResponse response = new BusResponse(bus);
                if (assignedBusIds.containsKey(bus.getId())) {
                    response.setIsAssigned(true);
                    ShippingBatch batch = assignedBusIds.get(bus.getId());
                    response.setAssignedBatchId(batch.getId());
                    response.setAssignedBatchStatus(batch.getStatus().name());
                }
                return response;
            })
            .collect(Collectors.toList());
    }

    public List<BusResponse> getAvailableBuses() {
        // Get all active buses that are not assigned to any non-delivered batch
        var assignedBusIds = shippingBatchRepository.findAll().stream()
            .filter(b -> b.getBus() != null)
            .filter(b -> b.getStatus() != ShippingStatus.DELIVERED && b.getStatus() != ShippingStatus.CANCELLED)
            .map(b -> b.getBus().getId())
            .collect(Collectors.toSet());
        
        return busRepository.findByIsActiveTrue().stream()
            .filter(bus -> !assignedBusIds.contains(bus.getId()))
            .map(BusResponse::new)
            .collect(Collectors.toList());
    }

    public BusResponse getBusById(Long id) {
        Bus bus = busRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Bus not found with id: " + id));
        
        BusResponse response = new BusResponse(bus);
        
        // Check if bus is assigned
        shippingBatchRepository.findAll().stream()
            .filter(b -> b.getBus() != null && b.getBus().getId().equals(id))
            .filter(b -> b.getStatus() != ShippingStatus.DELIVERED && b.getStatus() != ShippingStatus.CANCELLED)
            .findFirst()
            .ifPresent(batch -> {
                response.setIsAssigned(true);
                response.setAssignedBatchId(batch.getId());
                response.setAssignedBatchStatus(batch.getStatus().name());
            });
        
        return response;
    }

    // ========== HELPER METHODS ==========

    private int getAvailableBusCount() {
        var assignedBusIds = shippingBatchRepository.findAll().stream()
            .filter(b -> b.getBus() != null)
            .filter(b -> b.getStatus() != ShippingStatus.DELIVERED && b.getStatus() != ShippingStatus.CANCELLED)
            .map(b -> b.getBus().getId())
            .collect(Collectors.toSet());
        
        return (int) busRepository.findByIsActiveTrue().stream()
            .filter(bus -> !assignedBusIds.contains(bus.getId()))
            .count();
    }
}