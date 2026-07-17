package com.example.demo.shipping;

import com.example.demo.shipping.dtos.ShippingBatchResponse;
import com.example.demo.shipping.dtos.ShippingDashboardResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.shipping.dtos.BusResponse;
import com.example.demo.shipping.dtos.ShippingStatsResponse;

@RestController
@RequestMapping("/api/v1/shipping")
@Tag(name = "Shipping Data View", description = "Endpoints for viewing shipping data.")
@SecurityRequirement(name = "bearerAuth")
public class ShippingViewController {

    private final ShippingService shippingService;
    private final ShippingViewService shippingViewService;

    public ShippingViewController(ShippingService shippingService, 
                                  ShippingViewService shippingViewService) {
        this.shippingService = shippingService;
        this.shippingViewService = shippingViewService;
    }

    // ============================================================
    // BATCH VIEWING
    // ============================================================

    @GetMapping("/batches")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
        summary = "Get all shipping batches",
        description = "Retrieves a paginated list of all shipping batches with optional filters"
    )
    public ResponseEntity<Page<ShippingBatchResponse>> getBatches(
            @Parameter(description = "Filter by status (COLLECTING_ORDERS, READY_TO_DISPATCH, DISPATCHED, DELIVERED, CANCELLED)")
            @RequestParam(required = false) ShippingStatus status,
            
            @Parameter(description = "Filter by big area ID")
            @RequestParam(required = false) Long bigAreaId,
            
            @Parameter(description = "Filter by date from (ISO format)")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            
            @Parameter(description = "Filter by date to (ISO format)")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) 
            Pageable pageable) {
        
        return ResponseEntity.ok(shippingViewService.getBatches(status, bigAreaId, dateFrom, dateTo, pageable));
    }

    @GetMapping("/batches/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
        summary = "Get shipping batch by ID",
        description = "Retrieves a specific shipping batch with all its orders"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Batch retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Batch not found")
    })
    public ResponseEntity<ShippingBatchResponse> getBatchById(
            @Parameter(description = "Batch ID", required = true, example = "123")
            @PathVariable Long id) {
        return ResponseEntity.ok(shippingService.getBatchById(id));
    }

    @GetMapping("/batches/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
        summary = "Get batches by status",
        description = "Retrieves all shipping batches with a specific status"
    )
    public ResponseEntity<List<ShippingBatchResponse>> getBatchesByStatus(
            @Parameter(description = "Batch status", required = true, 
                      schema = @Schema(implementation = ShippingStatus.class))
            @PathVariable ShippingStatus status) {
        return ResponseEntity.ok(shippingService.getBatchesByStatus(status));
    }

    @GetMapping("/batches/big-area/{bigAreaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
        summary = "Get batches by big area",
        description = "Retrieves all shipping batches for a specific big area"
    )
    public ResponseEntity<List<ShippingBatchResponse>> getBatchesByBigArea(
            @Parameter(description = "Big area ID", required = true, example = "1")
            @PathVariable Long bigAreaId) {
        return ResponseEntity.ok(shippingService.getBatchesByBigAreaId(bigAreaId));
    }

    // ============================================================
    // ORDER TRACKING
    // ============================================================

    @GetMapping("/orders/{orderId}/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
        summary = "Get batch by order ID",
        description = "Retrieves the shipping batch that contains a specific order"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Batch found successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found in any batch")
    })
    public ResponseEntity<ShippingBatchResponse> getBatchByOrderId(
            @Parameter(description = "Order ID", required = true, example = "456")
            @PathVariable Long orderId) {
        return ResponseEntity.ok(shippingService.getBatchByOrderId(orderId));
    }

    // ============================================================
    // DASHBOARD & STATISTICS
    // ============================================================

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
        summary = "Get shipping dashboard",
        description = "Retrieves real-time shipping dashboard statistics"
    )
    public ResponseEntity<ShippingDashboardResponse> getDashboard() {
        return ResponseEntity.ok(shippingViewService.getDashboard());
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
        summary = "Get shipping statistics",
        description = "Retrieves shipping statistics for the specified period (default: last 30 days)"
    )
    public ResponseEntity<ShippingStatsResponse> getStats(
            @Parameter(description = "Start date (ISO format), default: 30 days ago")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            
            @Parameter(description = "End date (ISO format), default: now")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        
        return ResponseEntity.ok(shippingViewService.getStats(startDate, endDate));
    }

    // ============================================================
    // BUS VIEWING
    // ============================================================

    @GetMapping("/buses")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
        summary = "Get all buses",
        description = "Retrieves all buses with their current assignment status"
    )
    public ResponseEntity<List<BusResponse>> getBuses(
            @Parameter(description = "Filter by active status (true/false)")
            @RequestParam(required = false) Boolean isActive) {
        return ResponseEntity.ok(shippingViewService.getBuses(isActive));
    }

    @GetMapping("/buses/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
        summary = "Get available buses",
        description = "Retrieves all available (not assigned) buses"
    )
    public ResponseEntity<List<BusResponse>> getAvailableBuses() {
        return ResponseEntity.ok(shippingViewService.getAvailableBuses());
    }

    @GetMapping("/buses/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
        summary = "Get bus by ID",
        description = "Retrieves a specific bus with its assignment status"
    )
    public ResponseEntity<BusResponse> getBusById(
            @Parameter(description = "Bus ID", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(shippingViewService.getBusById(id));
    }
}