package com.example.demo.payment;

import com.example.demo.common.dtos.ApiResponse;
import com.example.demo.payment.dtos.PaymentRequest;
import com.example.demo.payment.dtos.PaymentResponse;
import com.example.demo.payment.dtos.RefundRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payment", description = "Endpoints for processing, confirming, refunding, and retrieving payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // ========== ✅ PROCESS PAYMENT (POST = PAY) ==========

    @PostMapping
    @Operation(summary = "Process a payment", description = "Processes a new payment based on the given request.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Payment processed successfully",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload or payment declined", content = @Content)
    })
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            @Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Payment processed successfully", response));
    }

    // ========== GET PAYMENT BY ID ==========

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by id", description = "Returns the payment identified by the given id.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Payment not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(
            @Parameter(description = "ID of the payment", required = true)
            @PathVariable Long id) {
        return paymentService.getPaymentById(id)
            .map(response -> ResponseEntity.ok(ApiResponse.success(response)))
            .orElse(ResponseEntity.notFound().build());
    }

    // ========== GET PAYMENT BY TRANSACTION REFERENCE ==========

    @GetMapping("/transaction/{transactionReference}")
    @Operation(summary = "Get payment by transaction reference", description = "Returns the payment matching the given transaction reference.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Payment not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByTransactionReference(
            @Parameter(description = "Transaction reference of the payment", required = true)
            @PathVariable String transactionReference) {
        return paymentService.getPaymentByTransactionReference(transactionReference)
            .map(response -> ResponseEntity.ok(ApiResponse.success(response)))
            .orElse(ResponseEntity.notFound().build());
    }

    // ========== GET PAYMENT BY ORDER ==========

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payment by order id", description = "Returns the payment associated with the given order.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Payment not found for order", content = @Content)
    })
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByOrderId(
            @Parameter(description = "ID of the order", required = true)
            @PathVariable Long orderId) {
        return paymentService.getPaymentByOrderId(orderId)
            .map(response -> ResponseEntity.ok(ApiResponse.success(response)))
            .orElse(ResponseEntity.notFound().build());
    }

    // ========== GET ALL PAYMENTS ==========

    @GetMapping
    @Operation(summary = "Get all payments", description = "Returns all payments in the system.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payments retrieved successfully")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAllPayments() {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getAllPayments()));
    }

    // ========== GET PAYMENTS BY STATUS ==========

    @GetMapping("/status/{status}")
    @Operation(summary = "Get payments by status", description = "Returns all payments currently in the given status.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payments retrieved successfully")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByStatus(
            @Parameter(description = "Status to filter payments by", required = true)
            @PathVariable PaymentStatus status) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentsByStatus(status)));
    }

    // ========== ✅ CONFIRM PAYMENT (MOCK) ==========

    @PostMapping("/confirm/{transactionReference}")
    @Operation(summary = "Confirm a payment", description = "Confirms the payment matching the given transaction reference (mock/simulated confirmation flow).")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment confirmed successfully",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Payment cannot be confirmed in its current state", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Payment not found for transaction reference", content = @Content)
    })
    public ResponseEntity<ApiResponse<PaymentResponse>> confirmPayment(
            @Parameter(description = "Transaction reference of the payment to confirm", required = true)
            @PathVariable String transactionReference) {
        PaymentResponse response = paymentService.confirmPayment(transactionReference);
        return ResponseEntity.ok(ApiResponse.success("Payment confirmed", response));
    }

    // ========== ✅ REFUND PAYMENT ==========

    @PostMapping("/refund")
    @Operation(summary = "Refund a payment", description = "Refunds a payment based on the given refund request.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment refunded successfully",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid refund request or payment not eligible for refund", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Payment not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<PaymentResponse>> refundPayment(
            @Valid @RequestBody RefundRequest request) {
        PaymentResponse response = paymentService.refundPayment(request);
        return ResponseEntity.ok(ApiResponse.success("Payment refunded", response));
    }
}