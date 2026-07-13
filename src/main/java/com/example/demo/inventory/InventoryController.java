package com.example.demo.inventory;

import com.example.demo.common.dtos.ApiResponse;
import com.example.demo.inventory.dtos.InventoryReportResponse;
import com.example.demo.inventory.dtos.InventoryTransactionResponse;
import com.example.demo.inventory.dtos.StockAdjustmentRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/v1/inventory")
@Tag(name = "Inventory", description = "Endpoints for inventory transactions, stock adjustments, reports, stock levels, and statistics. All endpoints require ADMIN, MANAGER, or INVENTORY_MANAGER role.")
@SecurityRequirement(name = "bearerAuth")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // ========== TRANSACTIONS ==========

    @GetMapping("/transactions")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Get all transactions", description = "Returns the full list of inventory transactions.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<InventoryTransactionResponse>>> getAllTransactions() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getAllTransactions()));
    }

    @GetMapping("/transactions/paged")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Get all transactions (paged)", description = "Returns a paginated list of inventory transactions.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<Page<InventoryTransactionResponse>>> getAllTransactionsPaged(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getAllTransactions(pageable)));
    }

    @GetMapping("/transactions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Get transaction by id", description = "Returns the inventory transaction identified by the given id.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transaction retrieved successfully",
                    content = @Content(schema = @Schema(implementation = InventoryTransactionResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<InventoryTransactionResponse>> getTransactionById(
            @Parameter(description = "ID of the transaction", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getTransactionById(id)));
    }

    @GetMapping("/transactions/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Get transactions by product", description = "Returns all inventory transactions for the given product.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<InventoryTransactionResponse>>> getTransactionsByProductId(
            @Parameter(description = "ID of the product", required = true)
            @PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getTransactionsByProductId(productId)));
    }

    @GetMapping("/transactions/product/{productId}/paged")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Get transactions by product (paged)", description = "Returns a paginated list of inventory transactions for the given product.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<Page<InventoryTransactionResponse>>> getTransactionsByProductIdPaged(
            @Parameter(description = "ID of the product", required = true)
            @PathVariable Long productId, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getTransactionsByProductId(productId, pageable)));
    }

    @GetMapping("/transactions/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Get transactions by type", description = "Returns all inventory transactions matching the given transaction type.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<InventoryTransactionResponse>>> getTransactionsByType(
            @Parameter(description = "Type of inventory transaction", required = true)
            @PathVariable InventoryTransactionType type) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getTransactionsByType(type)));
    }

    @GetMapping("/transactions/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Get transactions by date range", description = "Returns all inventory transactions that occurred within the given date-time range.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid date range", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<InventoryTransactionResponse>>> getTransactionsByDateRange(
            @Parameter(description = "Start of the date range (ISO date-time)", required = true, example = "2026-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End of the date range (ISO date-time)", required = true, example = "2026-01-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(ApiResponse.success(
            inventoryService.getTransactionsByDateRange(startDate, endDate)));
    }

    @GetMapping("/transactions/reference/{referenceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Get transactions by reference id", description = "Returns all inventory transactions linked to the given reference id (e.g. an order or purchase id).")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<InventoryTransactionResponse>>> getTransactionsByReferenceId(
            @Parameter(description = "Reference id linked to the transactions", required = true)
            @PathVariable Long referenceId) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getTransactionsByReferenceId(referenceId)));
    }

    // ========== STOCK ADJUSTMENT ==========

    @PostMapping("/adjust")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Adjust stock", description = "Creates a stock adjustment transaction (e.g. restock, damage, correction) for a product.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Stock adjusted successfully",
                    content = @Content(schema = @Schema(implementation = InventoryTransactionResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid adjustment payload", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<InventoryTransactionResponse>> adjustStock(
            @Valid @RequestBody StockAdjustmentRequest request,
            @Parameter(description = "ID of the user performing the adjustment", required = true)
            @RequestParam Long adjustedByUserId) {
        InventoryTransactionResponse response = inventoryService.adjustStock(request, adjustedByUserId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Stock adjusted successfully", response));
    }

    @GetMapping("/adjustments/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Get stock adjustments by product", description = "Returns all stock adjustment transactions for the given product.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Stock adjustments retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<InventoryTransactionResponse>>> getStockAdjustmentsByProductId(
            @Parameter(description = "ID of the product", required = true)
            @PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getStockAdjustmentsByProductId(productId)));
    }

    // ========== REPORTS ==========

    @GetMapping("/reports/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Get inventory report for a product", description = "Returns a detailed inventory report for the given product.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Report retrieved successfully",
                    content = @Content(schema = @Schema(implementation = InventoryReportResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<InventoryReportResponse>> getInventoryReportByProductId(
            @Parameter(description = "ID of the product", required = true)
            @PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getInventoryReportByProductId(productId)));
    }

    @GetMapping("/reports")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Get all inventory reports", description = "Returns inventory reports for all products.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reports retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<InventoryReportResponse>>> getAllInventoryReports() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getAllInventoryReports()));
    }

    // ========== STOCK LEVELS ==========

    @GetMapping("/stock/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Get current stock for a product", description = "Returns the current stock quantity for the given product.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Stock level retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<Integer>> getCurrentStockByProductId(
            @Parameter(description = "ID of the product", required = true)
            @PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getCurrentStockByProductId(productId)));
    }

    @GetMapping("/stock/low")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Get low-stock products", description = "Returns inventory reports for products whose stock is at or below the given threshold.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Low-stock products retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<InventoryReportResponse>>> getLowStockProducts(
            @Parameter(description = "Stock quantity threshold below which a product is considered low stock", example = "10")
            @RequestParam(defaultValue = "10") Integer threshold) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getLowStockProducts(threshold)));
    }

    @GetMapping("/stock/out-of-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Get out-of-stock products", description = "Returns inventory reports for products that currently have zero stock.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Out-of-stock products retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<InventoryReportResponse>>> getOutOfStockProducts() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getOutOfStockProducts()));
    }

    // ========== STATISTICS ==========

    @GetMapping("/stats/total-transactions")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Get total transaction count", description = "Returns the total number of inventory transactions recorded.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<Long>> getTotalTransactionCount() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getTotalTransactionCount()));
    }

    @GetMapping("/stats/transactions-by-type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Get transaction count by type", description = "Returns the number of inventory transactions matching the given type.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<Long>> getTransactionCountByType(
            @Parameter(description = "Type of inventory transaction", required = true)
            @PathVariable InventoryTransactionType type) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getTransactionCountByType(type)));
    }

    @GetMapping("/stats/total-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Get total stock value", description = "Returns the total stock quantity across all products.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Total stock retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<Integer>> getTotalStockValue() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getTotalStockValue()));
    }

    @GetMapping("/stats/received")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Get total received stock", description = "Returns the total quantity of stock received across all transactions.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Total received stock retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<Integer>> getTotalReceivedStock() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getTotalReceivedStock()));
    }

    @GetMapping("/stats/sold")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Get total sold stock", description = "Returns the total quantity of stock sold across all transactions.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Total sold stock retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<Integer>> getTotalSoldStock() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getTotalSoldStock()));
    }

    @GetMapping("/stats/damaged")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Get total damaged stock", description = "Returns the total quantity of stock recorded as damaged.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Total damaged stock retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<Integer>> getTotalDamagedStock() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getTotalDamagedStock()));
    }

    @GetMapping("/stats/returned")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Get total returned stock", description = "Returns the total quantity of stock recorded as returned.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Total returned stock retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<Integer>> getTotalReturnedStock() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getTotalReturnedStock()));
    }
}