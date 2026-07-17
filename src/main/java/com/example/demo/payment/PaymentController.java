package com.example.demo.payment;

import com.example.demo.common.dtos.ApiResponse;
import com.example.demo.payment.dtos.PaymentRequest;
import com.example.demo.payment.dtos.PaymentResponse;
import com.example.demo.payment.dtos.RefundRequest;
import com.example.demo.security.UserPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payment and Refund Process", description = "Endpoints for processing, confirming, refunding, and retrieving payments")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // ========== PROCESS PAYMENT ==========

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Process a payment", description = "Processes a new payment based on the given request.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Payment processed successfully",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload or payment declined", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            @Valid @RequestBody PaymentRequest request,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        PaymentResponse response = paymentService.processPayment(request, userPrincipal.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Payment processed successfully", response));
    }

    // ========== GET PAYMENT BY ID ==========

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Get payment by id", description = "Returns the payment identified by the given id.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Payment not found", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content)
    })
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(
            @Parameter(description = "ID of the payment", required = true)
            @PathVariable Long id,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        boolean isAdminOrManager = userPrincipal.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || 
                           a.getAuthority().equals("ROLE_MANAGER"));
        
        return paymentService.getPaymentById(id, userPrincipal.getId(), isAdminOrManager)
            .map(response -> ResponseEntity.ok(ApiResponse.success(response)))
            .orElse(ResponseEntity.notFound().build());
    }

    // ========== GET PAYMENT BY TRANSACTION REFERENCE ==========

    @GetMapping("/transaction/{transactionReference}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Get payment by transaction reference", description = "Returns the payment matching the given transaction reference.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Payment not found", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content)
    })
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByTransactionReference(
            @Parameter(description = "Transaction reference of the payment", required = true)
            @PathVariable String transactionReference,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        boolean isAdminOrManager = userPrincipal.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || 
                           a.getAuthority().equals("ROLE_MANAGER"));
        
        return paymentService.getPaymentByTransactionReference(transactionReference, userPrincipal.getId(), isAdminOrManager)
            .map(response -> ResponseEntity.ok(ApiResponse.success(response)))
            .orElse(ResponseEntity.notFound().build());
    }

    // ========== GET PAYMENT BY ORDER ==========

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Get payment by order id", description = "Returns the payment associated with the given order.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Payment not found for order", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content)
    })
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByOrderId(
            @Parameter(description = "ID of the order", required = true)
            @PathVariable Long orderId,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        boolean isAdminOrManager = userPrincipal.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || 
                           a.getAuthority().equals("ROLE_MANAGER"));
        
        return paymentService.getPaymentByOrderId(orderId, userPrincipal.getId(), isAdminOrManager)
            .map(response -> ResponseEntity.ok(ApiResponse.success(response)))
            .orElse(ResponseEntity.notFound().build());
    }

    // ========== GET ALL PAYMENTS (Admin/Manager only) ==========

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all payments", description = "Returns all payments in the system. Admin/Manager only.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payments retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAllPayments() {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getAllPayments()));
    }

    // ========== GET PAYMENTS BY STATUS (Admin/Manager only) ==========

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get payments by status", description = "Returns all payments currently in the given status. Admin/Manager only.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payments retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByStatus(
            @Parameter(description = "Status to filter payments by", required = true)
            @PathVariable PaymentStatus status) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentsByStatus(status)));
    }

    // ========== CONFIRM PAYMENT (Admin/Manager only) ==========

    @PostMapping("/confirm/{transactionReference}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Confirm a payment", description = "Confirms the payment matching the given transaction reference. Admin/Manager only.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment confirmed successfully",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Payment cannot be confirmed in its current state", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Payment not found for transaction reference", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<PaymentResponse>> confirmPayment(
            @Parameter(description = "Transaction reference of the payment to confirm", required = true)
            @PathVariable String transactionReference) {
        PaymentResponse response = paymentService.confirmPayment(transactionReference);
        return ResponseEntity.ok(ApiResponse.success("Payment confirmed", response));
    }

    // ========== REFUND PAYMENT (Admin/Manager can refund any, User can refund own) ==========

    @PostMapping("/refund")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(
        summary = "Refund a payment by order ID",
        description = "Refunds a payment based on the order ID. Restores inventory and cancels the order.\n\n" +
                      "🔑 **Permissions:**\n" +
                      "- **ADMIN/MANAGER:** Can refund ANY order (100% refund)\n" +
                      "- **USER:** Can ONLY refund their OWN orders (2/3 refund, 1/3 fee) and only if order is NOT shipped or delivered"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment refunded successfully",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid refund request or payment not eligible for refund", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "You cannot refund this order", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order or payment not found", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content)
    })
    public ResponseEntity<ApiResponse<PaymentResponse>> refundPayment(
            @Valid @RequestBody RefundRequest request,
            Authentication authentication) {
        
        // Get current user from authentication
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getId();
        
        // Check if user is admin or manager
        boolean isAdminOrManager = userPrincipal.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || 
                           a.getAuthority().equals("ROLE_MANAGER"));
        
        PaymentResponse response = paymentService.refundPayment(request, userId, isAdminOrManager);
        return ResponseEntity.ok(ApiResponse.success("Payment refunded successfully", response));
    }
}