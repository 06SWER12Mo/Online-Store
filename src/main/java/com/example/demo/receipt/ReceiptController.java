package com.example.demo.receipt;

import com.example.demo.common.dtos.ApiResponse;
import com.example.demo.receipt.dtos.ReceiptRequest;
import com.example.demo.receipt.dtos.ReceiptResponse;
import com.example.demo.receipt.dtos.SupplierRequest;
import com.example.demo.receipt.dtos.SupplierResponse;
import com.example.demo.security.UserPrincipal;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;

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
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/receipts")
@Tag(name = "Receipts", description = "Endpoints for managing receipts and suppliers")
@SecurityRequirement(name = "bearerAuth")
public class ReceiptController {

    private final ReceiptService receiptService;
    private final UserRepository userRepository;  // ✅ ADD THIS

    public ReceiptController(ReceiptService receiptService, UserRepository userRepository) {
        this.receiptService = receiptService;
        this.userRepository = userRepository;  // ✅ ADD THIS
    }

    // ========== RECEIPT ENDPOINTS ==========

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(
            summary = "Create a new receipt",
            description = "Creates a receipt for a supplier and updates stock levels accordingly. " +
                    "Requires ADMIN, MANAGER, or INVENTORY_MANAGER role."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "Receipt created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ReceiptResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - insufficient role")
    })
    public ResponseEntity<ApiResponse<ReceiptResponse>> createReceipt(@Valid @RequestBody ReceiptRequest request) {
        Long userId = getCurrentUserId();
        ReceiptResponse response = receiptService.createReceipt(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Receipt created successfully. Stock updated.", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Get receipt by ID", description = "Retrieves a single receipt by its internal ID.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Receipt found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ReceiptResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Receipt not found")
    })
    public ResponseEntity<ApiResponse<ReceiptResponse>> getReceiptById(
            @Parameter(description = "ID of the receipt to retrieve", required = true) @PathVariable Long id) {
        ReceiptResponse response = receiptService.getReceiptById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/number/{receiptNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Get receipt by receipt number", description = "Retrieves a single receipt using its unique receipt number.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Receipt found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Receipt not found")
    })
    public ResponseEntity<ApiResponse<ReceiptResponse>> getReceiptByNumber(
            @Parameter(description = "Unique receipt number", required = true) @PathVariable String receiptNumber) {
        ReceiptResponse response = receiptService.getReceiptByNumber(receiptNumber);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Get all receipts", description = "Retrieves a paginated list of all receipts, sorted by creation date descending by default.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Receipts retrieved successfully")
    })
    public ResponseEntity<ApiResponse<Page<ReceiptResponse>>> getAllReceipts(
            @Parameter(description = "Pagination and sorting parameters")
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ReceiptResponse> response = receiptService.getAllReceipts(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/supplier/{supplierId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Get receipts by supplier", description = "Retrieves a paginated list of receipts for a specific supplier.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Receipts retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<ApiResponse<Page<ReceiptResponse>>> getReceiptsBySupplier(
            @Parameter(description = "ID of the supplier", required = true) @PathVariable Long supplierId,
            @Parameter(description = "Pagination and sorting parameters")
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ReceiptResponse> response = receiptService.getReceiptsBySupplier(supplierId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Get receipts by status", description = "Retrieves a paginated list of receipts filtered by status (e.g. PENDING, APPROVED, REJECTED).")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Receipts retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid status value")
    })
    public ResponseEntity<ApiResponse<Page<ReceiptResponse>>> getReceiptsByStatus(
            @Parameter(description = "Receipt status to filter by", required = true) @PathVariable String status,
            @Parameter(description = "Pagination and sorting parameters")
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ReceiptResponse> response = receiptService.getReceiptsByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Update receipt status", description = "Updates the status of an existing receipt (e.g. PENDING, APPROVED, REJECTED).")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid status value"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Receipt not found")
    })
    public ResponseEntity<ApiResponse<ReceiptResponse>> updateReceiptStatus(
            @Parameter(description = "ID of the receipt to update", required = true) @PathVariable Long id,
            @Parameter(description = "New status value", required = true) @RequestParam String status) {
        ReceiptResponse response = receiptService.updateReceiptStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Status updated to: " + status, response));
    }

    @PatchMapping("/{id}/payment-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Update receipt payment status", description = "Updates the payment status of an existing receipt (e.g. UNPAID, PARTIAL, PAID).")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment status updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid payment status value"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Receipt not found")
    })
    public ResponseEntity<ApiResponse<ReceiptResponse>> updatePaymentStatus(
            @Parameter(description = "ID of the receipt to update", required = true) @PathVariable Long id,
            @Parameter(description = "New payment status value", required = true) @RequestParam String paymentStatus) {
        ReceiptResponse response = receiptService.updatePaymentStatus(id, paymentStatus);
        return ResponseEntity.ok(ApiResponse.success("Payment status updated to: " + paymentStatus, response));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Approve a receipt", description = "Marks a receipt as approved. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Receipt approved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - insufficient role"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Receipt not found")
    })
    public ResponseEntity<ApiResponse<ReceiptResponse>> approveReceipt(
            @Parameter(description = "ID of the receipt to approve", required = true) @PathVariable Long id) {
        Long userId = getCurrentUserId();
        ReceiptResponse response = receiptService.approveReceipt(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Receipt approved successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Delete a receipt", description = "Deletes a receipt by ID. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Receipt deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - insufficient role"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Receipt not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteReceipt(
            @Parameter(description = "ID of the receipt to delete", required = true) @PathVariable Long id) {
        receiptService.deleteReceipt(id);
        return ResponseEntity.ok(ApiResponse.success("Receipt deleted successfully"));
    }

    // ========== RECEIPT STATISTICS ==========

    @GetMapping("/stats/total-between")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get total receipts amount between dates",
            description = "Calculates the total monetary amount of receipts created within a date range. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Total calculated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid date range")
    })
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalReceiptsAmountBetween(
            @Parameter(description = "Start of the date range (ISO-8601)", required = true) @RequestParam LocalDateTime startDate,
            @Parameter(description = "End of the date range (ISO-8601)", required = true) @RequestParam LocalDateTime endDate) {
        BigDecimal total = receiptService.getTotalReceiptsAmountBetween(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(total));
    }

    @GetMapping("/stats/count-approved-between")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Count approved receipts between dates",
            description = "Counts the number of approved receipts created within a date range. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Count calculated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid date range")
    })
    public ResponseEntity<ApiResponse<Long>> countApprovedReceiptsBetween(
            @Parameter(description = "Start of the date range (ISO-8601)", required = true) @RequestParam LocalDateTime startDate,
            @Parameter(description = "End of the date range (ISO-8601)", required = true) @RequestParam LocalDateTime endDate) {
        long count = receiptService.countApprovedReceiptsBetween(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    // ========== SUPPLIER ENDPOINTS ==========

    @PostMapping("/suppliers")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create a new supplier", description = "Creates a new supplier record. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "Supplier created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SupplierResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Supplier already exists")
    })
    public ResponseEntity<ApiResponse<SupplierResponse>> createSupplier(@Valid @RequestBody SupplierRequest request) {
        SupplierResponse response = receiptService.createSupplier(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Supplier created successfully", response));
    }

    @PutMapping("/suppliers/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update a supplier", description = "Updates an existing supplier's details. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Supplier updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<ApiResponse<SupplierResponse>> updateSupplier(
            @Parameter(description = "ID of the supplier to update", required = true) @PathVariable Long id,
            @Valid @RequestBody SupplierRequest request) {
        SupplierResponse response = receiptService.updateSupplier(id, request);
        return ResponseEntity.ok(ApiResponse.success("Supplier updated successfully", response));
    }

    @DeleteMapping("/suppliers/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Delete a supplier", description = "Deletes a supplier by ID. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Supplier deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(
            @Parameter(description = "ID of the supplier to delete", required = true) @PathVariable Long id) {
        receiptService.deleteSupplier(id);
        return ResponseEntity.ok(ApiResponse.success("Supplier deleted successfully"));
    }

    @GetMapping("/suppliers/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Get supplier by ID", description = "Retrieves a single supplier by its ID.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Supplier found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SupplierResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<ApiResponse<SupplierResponse>> getSupplierById(
            @Parameter(description = "ID of the supplier to retrieve", required = true) @PathVariable Long id) {
        SupplierResponse response = receiptService.getSupplierById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/suppliers")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Get all suppliers", description = "Retrieves a paginated list of all suppliers, sorted by name ascending by default.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Suppliers retrieved successfully")
    })
    public ResponseEntity<ApiResponse<Page<SupplierResponse>>> getAllSuppliers(
            @Parameter(description = "Pagination and sorting parameters")
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<SupplierResponse> response = receiptService.getAllSuppliers(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/suppliers/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY_MANAGER')")
    @Operation(summary = "Search suppliers", description = "Searches suppliers by a keyword matching name or other identifying fields.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search results returned successfully")
    })
    public ResponseEntity<ApiResponse<List<SupplierResponse>>> searchSuppliers(
            @Parameter(description = "Keyword to search for", required = true) @RequestParam String keyword) {
        List<SupplierResponse> response = receiptService.searchSuppliers(keyword);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/suppliers/{id}/toggle-active")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Toggle supplier active status", description = "Toggles a supplier between active and inactive states. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Supplier status toggled successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<ApiResponse<Void>> toggleSupplierActive(
            @Parameter(description = "ID of the supplier to toggle", required = true) @PathVariable Long id) {
        receiptService.toggleSupplierActive(id);
        return ResponseEntity.ok(ApiResponse.success("Supplier status toggled"));
    }

    // ========== HELPER METHODS ==========

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserPrincipal) {
                return ((UserPrincipal) principal).getId();
            }

            String username = authentication.getName();
            
            User user = userRepository.findByUsername(username)
                    .or(() -> userRepository.findByEmail(username))
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return user.getId();
        }
        throw new RuntimeException("User not authenticated");
    }
}