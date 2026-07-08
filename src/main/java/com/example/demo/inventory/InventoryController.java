package com.example.demo.inventory;

import com.example.demo.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // ========== TRANSACTION ENDPOINTS ==========

    @GetMapping("/transactions")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    public ResponseEntity<ApiResponse<List<InventoryTransactionResponse>>> getAllTransactions() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getAllTransactions()));
    }

    @GetMapping("/transactions/paged")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    public ResponseEntity<ApiResponse<Page<InventoryTransactionResponse>>> getAllTransactionsPaged(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getAllTransactions(pageable)));
    }

    @GetMapping("/transactions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    public ResponseEntity<ApiResponse<InventoryTransactionResponse>> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getTransactionById(id)));
    }

    @GetMapping("/transactions/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    public ResponseEntity<ApiResponse<List<InventoryTransactionResponse>>> getTransactionsByProductId(
            @PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getTransactionsByProductId(productId)));
    }

    @GetMapping("/transactions/product/{productId}/paged")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    public ResponseEntity<ApiResponse<Page<InventoryTransactionResponse>>> getTransactionsByProductIdPaged(
            @PathVariable Long productId, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getTransactionsByProductId(productId, pageable)));
    }

    @GetMapping("/transactions/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    public ResponseEntity<ApiResponse<List<InventoryTransactionResponse>>> getTransactionsByType(
            @PathVariable InventoryTransactionType type) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getTransactionsByType(type)));
    }

    @GetMapping("/transactions/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    public ResponseEntity<ApiResponse<List<InventoryTransactionResponse>>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getTransactionsByDateRange(startDate, endDate)));
    }

    @GetMapping("/transactions/reference")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    public ResponseEntity<ApiResponse<List<InventoryTransactionResponse>>> getTransactionsByReference(
            @RequestParam InventoryReferenceType referenceType,
            @RequestParam Long referenceId) {
        return ResponseEntity.ok(ApiResponse.success(
            inventoryService.getTransactionsByReference(referenceType, referenceId)));
    }

    // ========== STOCK ADJUSTMENT ENDPOINTS ==========

    @PostMapping("/adjust")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    public ResponseEntity<ApiResponse<InventoryTransactionResponse>> adjustStock(
            @Valid @RequestBody StockAdjustmentRequest request,
            @RequestParam Long adjustedByUserId) {
        InventoryTransactionResponse response = inventoryService.adjustStock(request, adjustedByUserId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Stock adjusted successfully", response));
    }

    @GetMapping("/adjustments/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    public ResponseEntity<ApiResponse<List<InventoryTransactionResponse>>> getStockAdjustmentsByProductId(
            @PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getStockAdjustmentsByProductId(productId)));
    }

    // ========== INVENTORY REPORT ENDPOINTS ==========

    @GetMapping("/reports/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    public ResponseEntity<ApiResponse<InventoryReportResponse>> getInventoryReportByProductId(
            @PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getInventoryReportByProductId(productId)));
    }

    @GetMapping("/reports")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    public ResponseEntity<ApiResponse<List<InventoryReportResponse>>> getAllInventoryReports() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getAllInventoryReports()));
    }

    // ========== STOCK LEVEL ENDPOINTS ==========

    @GetMapping("/stock/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    public ResponseEntity<ApiResponse<Integer>> getCurrentStockByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getCurrentStockByProductId(productId)));
    }

    @GetMapping("/stock/low")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    public ResponseEntity<ApiResponse<List<InventoryReportResponse>>> getLowStockProducts(
            @RequestParam(defaultValue = "10") Integer threshold) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getLowStockProducts(threshold)));
    }

    @GetMapping("/stock/out-of-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    public ResponseEntity<ApiResponse<List<InventoryReportResponse>>> getOutOfStockProducts() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getOutOfStockProducts()));
    }

    // ========== DASHBOARD STATISTICS ENDPOINTS ==========

    @GetMapping("/stats/total-transactions")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    public ResponseEntity<ApiResponse<Long>> getTotalTransactionCount() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getTotalTransactionCount()));
    }

    @GetMapping("/stats/transactions-by-type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    public ResponseEntity<ApiResponse<Long>> getTransactionCountByType(
            @PathVariable InventoryTransactionType type) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getTransactionCountByType(type)));
    }

    @GetMapping("/stats/total-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    public ResponseEntity<ApiResponse<Integer>> getTotalStockValue() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getTotalStockValue()));
    }
}