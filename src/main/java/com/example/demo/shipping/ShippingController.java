package com.example.demo.shipping;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipping")
public class ShippingController {

    private final ShippingService shippingService;
    private final BatchSchedulerService batchSchedulerService;

    public ShippingController(ShippingService shippingService,
                              BatchSchedulerService batchSchedulerService) {
        this.shippingService = shippingService;
        this.batchSchedulerService = batchSchedulerService;
    }

    // ========== BATCH MANAGEMENT ==========

    @PostMapping("/batches")
    public ResponseEntity<ShippingBatchResponse> createBatch(
            @RequestParam Long bigAreaId,
            @RequestParam(defaultValue = "10") Integer minimumOrders) {
        ShippingBatchResponse response = shippingService.createBatch(bigAreaId, minimumOrders);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/batches")
    public ResponseEntity<List<ShippingBatchResponse>> getAllBatches() {
        return ResponseEntity.ok(shippingService.getAllBatches());
    }

    @GetMapping("/batches/{id}")
    public ResponseEntity<ShippingBatchResponse> getBatchById(@PathVariable Long id) {
        return ResponseEntity.ok(shippingService.getBatchById(id));
    }

    @GetMapping("/batches/status/{status}")
    public ResponseEntity<List<ShippingBatchResponse>> getBatchesByStatus(@PathVariable ShippingStatus status) {
        return ResponseEntity.ok(shippingService.getBatchesByStatus(status));
    }

    @GetMapping("/batches/big-area/{bigAreaId}")
    public ResponseEntity<List<ShippingBatchResponse>> getBatchesByBigAreaId(@PathVariable Long bigAreaId) {
        return ResponseEntity.ok(shippingService.getBatchesByBigAreaId(bigAreaId));
    }

    // ========== ORDER ASSIGNMENT ==========

    @PostMapping("/batches/{batchId}/orders/{orderId}")
    public ResponseEntity<ShippingBatchResponse> addOrderToBatch(
            @PathVariable Long batchId,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(shippingService.addOrderToBatch(batchId, orderId));
    }

    @DeleteMapping("/batches/{batchId}/orders/{orderId}")
    public ResponseEntity<ShippingBatchResponse> removeOrderFromBatch(
            @PathVariable Long batchId,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(shippingService.removeOrderFromBatch(batchId, orderId));
    }

    // ========== BUS ASSIGNMENT ==========

    @PostMapping("/batches/assign-bus")
    public ResponseEntity<ShippingBatchResponse> assignBusToBatch(
            @Valid @RequestBody AssignBusRequest request) {
        return ResponseEntity.ok(shippingService.assignBusToBatch(request));
    }

    // ========== BATCH LIFECYCLE ==========

    @PostMapping("/batches/{batchId}/ready")
    public ResponseEntity<ShippingBatchResponse> markBatchReadyToDispatch(@PathVariable Long batchId) {
        return ResponseEntity.ok(shippingService.markBatchReadyToDispatch(batchId));
    }

    @PostMapping("/batches/{batchId}/dispatch")
    public ResponseEntity<ShippingBatchResponse> dispatchBatch(@PathVariable Long batchId) {
        return ResponseEntity.ok(shippingService.dispatchBatch(batchId));
    }

    @PostMapping("/batches/deliver")
    public ResponseEntity<ShippingBatchResponse> confirmDelivery(
            @Valid @RequestBody DeliveryConfirmationRequest request) {
        return ResponseEntity.ok(shippingService.confirmDelivery(request));
    }

    @PostMapping("/batches/{batchId}/cancel")
    public ResponseEntity<ShippingBatchResponse> cancelBatch(@PathVariable Long batchId) {
        return ResponseEntity.ok(shippingService.cancelBatch(batchId));
    }

    // ========== ORDER TRACKING ==========

    @GetMapping("/orders/{orderId}/batch")
    public ResponseEntity<ShippingBatchResponse> getBatchByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(shippingService.getBatchByOrderId(orderId));
    }

    @GetMapping("/orders/{orderId}/in-batch")
    public ResponseEntity<Boolean> isOrderInBatch(@PathVariable Long orderId) {
        return ResponseEntity.ok(shippingService.isOrderInBatch(orderId));
    }

    // ========== SCHEDULER OPERATIONS ==========

    @PostMapping("/scheduler/auto-create")
    public ResponseEntity<Void> triggerAutoCreateBatches() {
        batchSchedulerService.autoCreateBatchesForBigAreas();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/scheduler/auto-dispatch")
    public ResponseEntity<Void> triggerAutoDispatch() {
        batchSchedulerService.autoDispatchReadyBatches();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/scheduler/pending-dispatches")
    public ResponseEntity<List<ShippingBatchResponse>> getPendingDispatches() {
        return ResponseEntity.ok(batchSchedulerService.getPendingDispatches());
    }
}