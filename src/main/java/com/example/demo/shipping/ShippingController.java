/*

Commented out because this version is meant to be for real life business use, not just a demo.
The current shipping controller is now ShippingViewController, where every thing is automated, and the admin only
views data without need to manually trigger any action.

package com.example.demo.shipping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.shipping.dtos.AssignBusRequest;
import com.example.demo.shipping.dtos.DeliveryConfirmationRequest;
import com.example.demo.shipping.dtos.ShippingBatchResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shipping")
@Tag(name = "Shipping Management", description = "Endpoints for managing shipping batches, orders, and deliveries")
public class ShippingController {

    private final ShippingService shippingService;
    private final BatchSchedulerService batchSchedulerService;

    public ShippingController(ShippingService shippingService,
                              BatchSchedulerService batchSchedulerService) {
        this.shippingService = shippingService;
        this.batchSchedulerService = batchSchedulerService;
    }

    // ========== BATCH MANAGEMENT ==========

    @Operation(
        summary = "Create a new shipping batch",
        description = "Creates a new shipping batch for a specific big area with minimum order threshold"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Batch created successfully",
                    content = @Content(schema = @Schema(implementation = ShippingBatchResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input parameters"),
        @ApiResponse(responseCode = "404", description = "Big area not found")
    })
    @PostMapping("/batches")
    public ResponseEntity<ShippingBatchResponse> createBatch(
            @Parameter(description = "ID of the big area", required = true, example = "1")
            @RequestParam Long bigAreaId,
            @Parameter(description = "Minimum number of orders to create batch", example = "10")
            @RequestParam(defaultValue = "10") Integer minimumOrders) {
        ShippingBatchResponse response = shippingService.createBatch(bigAreaId, minimumOrders);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "Get all shipping batches",
        description = "Retrieves a list of all shipping batches"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all batches")
    @GetMapping("/batches")
    public ResponseEntity<List<ShippingBatchResponse>> getAllBatches() {
        return ResponseEntity.ok(shippingService.getAllBatches());
    }

    @Operation(
        summary = "Get shipping batch by ID",
        description = "Retrieves a specific shipping batch by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Batch found successfully"),
        @ApiResponse(responseCode = "404", description = "Batch not found")
    })
    @GetMapping("/batches/{id}")
    public ResponseEntity<ShippingBatchResponse> getBatchById(
            @Parameter(description = "ID of the batch to retrieve", required = true, example = "123")
            @PathVariable Long id) {
        return ResponseEntity.ok(shippingService.getBatchById(id));
    }

    @Operation(
        summary = "Get batches by status",
        description = "Retrieves all shipping batches with a specific status"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved batches by status")
    @GetMapping("/batches/status/{status}")
    public ResponseEntity<List<ShippingBatchResponse>> getBatchesByStatus(
            @Parameter(description = "Status of the batches", required = true, 
                      schema = @Schema(implementation = ShippingStatus.class))
            @PathVariable ShippingStatus status) {
        return ResponseEntity.ok(shippingService.getBatchesByStatus(status));
    }

    @Operation(
        summary = "Get batches by big area",
        description = "Retrieves all shipping batches for a specific big area"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved batches for the big area")
    @GetMapping("/batches/big-area/{bigAreaId}")
    public ResponseEntity<List<ShippingBatchResponse>> getBatchesByBigAreaId(
            @Parameter(description = "ID of the big area", required = true, example = "1")
            @PathVariable Long bigAreaId) {
        return ResponseEntity.ok(shippingService.getBatchesByBigAreaId(bigAreaId));
    }

    // ========== ORDER ASSIGNMENT ==========

    @Operation(
        summary = "Add order to batch",
        description = "Assigns a specific order to an existing shipping batch"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order added to batch successfully"),
        @ApiResponse(responseCode = "400", description = "Batch is already dispatched or cancelled"),
        @ApiResponse(responseCode = "404", description = "Batch or order not found"),
        @ApiResponse(responseCode = "409", description = "Order already in another batch")
    })
    @PostMapping("/batches/{batchId}/orders/{orderId}")
    public ResponseEntity<ShippingBatchResponse> addOrderToBatch(
            @Parameter(description = "ID of the batch", required = true, example = "123")
            @PathVariable Long batchId,
            @Parameter(description = "ID of the order", required = true, example = "456")
            @PathVariable Long orderId) {
        return ResponseEntity.ok(shippingService.addOrderToBatch(batchId, orderId));
    }

    @Operation(
        summary = "Remove order from batch",
        description = "Removes a specific order from its assigned shipping batch"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order removed from batch successfully"),
        @ApiResponse(responseCode = "400", description = "Batch is already dispatched or cancelled"),
        @ApiResponse(responseCode = "404", description = "Batch or order not found")
    })
    @DeleteMapping("/batches/{batchId}/orders/{orderId}")
    public ResponseEntity<ShippingBatchResponse> removeOrderFromBatch(
            @Parameter(description = "ID of the batch", required = true, example = "123")
            @PathVariable Long batchId,
            @Parameter(description = "ID of the order", required = true, example = "456")
            @PathVariable Long orderId) {
        return ResponseEntity.ok(shippingService.removeOrderFromBatch(batchId, orderId));
    }

    // ========== BUS ASSIGNMENT ==========

    @Operation(
        summary = "Manually assign bus to batch",
        description = "Manually assigns a specific bus to a shipping batch (fallback method)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Bus assigned successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid bus assignment request"),
        @ApiResponse(responseCode = "404", description = "Batch or bus not found")
    })
    @PostMapping("/batches/assign-bus")
    public ResponseEntity<ShippingBatchResponse> assignBusToBatch(
            @Valid @RequestBody AssignBusRequest request) {
        return ResponseEntity.ok(shippingService.assignBusToBatch(request));
    }

    @Operation(
        summary = "Auto-assign bus to batch",
        description = "Automatically assigns the most suitable available bus to a batch based on capacity and location"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Bus auto-assigned successfully"),
        @ApiResponse(responseCode = "400", description = "Batch is not in correct state for bus assignment"),
        @ApiResponse(responseCode = "404", description = "Batch not found"),
        @ApiResponse(responseCode = "503", description = "No available buses found")
    })
    @PostMapping("/batches/{batchId}/auto-assign-bus")
    public ResponseEntity<ShippingBatchResponse> autoAssignBus(
            @Parameter(description = "ID of the batch", required = true, example = "123")
            @PathVariable Long batchId) {
        return ResponseEntity.ok(shippingService.autoAssignBus(batchId));
    }

    // ========== BATCH LIFECYCLE ==========

    @Operation(
        summary = "Mark batch as ready to dispatch",
        description = "Changes the batch status to READY_FOR_DISPATCH once all orders are assigned"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Batch marked as ready"),
        @ApiResponse(responseCode = "400", description = "Batch has no orders or bus not assigned"),
        @ApiResponse(responseCode = "404", description = "Batch not found")
    })
    @PostMapping("/batches/{batchId}/ready")
    public ResponseEntity<ShippingBatchResponse> markBatchReadyToDispatch(
            @Parameter(description = "ID of the batch", required = true, example = "123")
            @PathVariable Long batchId) {
        return ResponseEntity.ok(shippingService.markBatchReadyToDispatch(batchId));
    }

    @Operation(
        summary = "Dispatch batch",
        description = "Dispatching a batch that is ready, updates status to DISPATCHED"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Batch dispatched successfully"),
        @ApiResponse(responseCode = "400", description = "Batch is not ready for dispatch"),
        @ApiResponse(responseCode = "404", description = "Batch not found")
    })
    @PostMapping("/batches/{batchId}/dispatch")
    public ResponseEntity<ShippingBatchResponse> dispatchBatch(
            @Parameter(description = "ID of the batch", required = true, example = "123")
            @PathVariable Long batchId) {
        return ResponseEntity.ok(shippingService.dispatchBatch(batchId));
    }

    @Operation(
        summary = "Auto-deliver batch",
        description = "Automatically marks all orders in a batch as delivered based on GPS or driver confirmation"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Batch delivered successfully"),
        @ApiResponse(responseCode = "400", description = "Batch is not in dispatched state"),
        @ApiResponse(responseCode = "404", description = "Batch not found")
    })
    @PostMapping("/batches/{batchId}/auto-deliver")
    public ResponseEntity<ShippingBatchResponse> autoDeliverBatch(
            @Parameter(description = "ID of the batch", required = true, example = "123")
            @PathVariable Long batchId) {
        return ResponseEntity.ok(shippingService.autoDeliverBatch(batchId));
    }

    @Operation(
        summary = "Confirm delivery",
        description = "Manually confirms delivery for all orders in a batch with delivery details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delivery confirmed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid delivery confirmation request"),
        @ApiResponse(responseCode = "404", description = "Batch not found")
    })
    @PostMapping("/batches/deliver")
    public ResponseEntity<ShippingBatchResponse> confirmDelivery(
            @Valid @RequestBody DeliveryConfirmationRequest request) {
        return ResponseEntity.ok(shippingService.confirmDelivery(request));
    }

    @Operation(
        summary = "Cancel batch",
        description = "Cancels a shipping batch and releases all assigned orders"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Batch cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot cancel dispatched or delivered batch"),
        @ApiResponse(responseCode = "404", description = "Batch not found")
    })
    @PostMapping("/batches/{batchId}/cancel")
    public ResponseEntity<ShippingBatchResponse> cancelBatch(
            @Parameter(description = "ID of the batch", required = true, example = "123")
            @PathVariable Long batchId) {
        return ResponseEntity.ok(shippingService.cancelBatch(batchId));
    }

    // ========== ORDER TRACKING ==========

    @Operation(
        summary = "Get batch by order ID",
        description = "Retrieves the shipping batch that contains a specific order"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Batch found successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found or not in any batch")
    })
    @GetMapping("/orders/{orderId}/batch")
    public ResponseEntity<ShippingBatchResponse> getBatchByOrderId(
            @Parameter(description = "ID of the order", required = true, example = "456")
            @PathVariable Long orderId) {
        return ResponseEntity.ok(shippingService.getBatchByOrderId(orderId));
    }

    @Operation(
        summary = "Check if order is in a batch",
        description = "Checks whether a specific order is currently assigned to any shipping batch"
    )
    @ApiResponse(responseCode = "200", description = "Order status checked successfully")
    @GetMapping("/orders/{orderId}/in-batch")
    public ResponseEntity<Boolean> isOrderInBatch(
            @Parameter(description = "ID of the order", required = true, example = "456")
            @PathVariable Long orderId) {
        return ResponseEntity.ok(shippingService.isOrderInBatch(orderId));
    }

    // ========== SCHEDULER OPERATIONS ==========

    @Operation(
        summary = "Trigger auto-create batches",
        description = "Manually triggers the scheduled job to automatically create batches for all big areas"
    )
    @ApiResponse(responseCode = "200", description = "Auto-create job triggered successfully")
    @PostMapping("/scheduler/auto-create")
    public ResponseEntity<Void> triggerAutoCreateBatches() {
        batchSchedulerService.autoCreateBatchesForBigAreas();
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Trigger auto-dispatch",
        description = "Manually triggers the scheduled job to automatically dispatch ready batches"
    )
    @ApiResponse(responseCode = "200", description = "Auto-dispatch job triggered successfully")
    @PostMapping("/scheduler/auto-dispatch")
    public ResponseEntity<Void> triggerAutoDispatch() {
        batchSchedulerService.autoDispatchReadyBatches();
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Trigger auto-deliver",
        description = "Manually triggers the scheduled job to automatically deliver dispatched batches"
    )
    @ApiResponse(responseCode = "200", description = "Auto-deliver job triggered successfully")
    @PostMapping("/scheduler/auto-deliver")
    public ResponseEntity<Void> triggerAutoDeliver() {
        batchSchedulerService.autoDeliverBatches();
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Get pending dispatches",
        description = "Retrieves all batches that are ready for dispatch but not yet dispatched"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved pending dispatches")
   @GetMapping("/scheduler/pending-dispatches")
    public ResponseEntity<List<ShippingBatchResponse>> getPendingDispatches() {
        return ResponseEntity.ok(batchSchedulerService.getPendingDispatches());
    }
}


*/