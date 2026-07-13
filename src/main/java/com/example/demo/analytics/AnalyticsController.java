package com.example.demo.analytics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.analytics.dtos.CategoryAnalyticsResponse;
import com.example.demo.analytics.dtos.DashboardResponse;
import com.example.demo.analytics.dtos.GeographicReportResponse;
import com.example.demo.analytics.dtos.ProductAnalyticsResponse;
import com.example.demo.analytics.dtos.SalesReportResponse;
import com.example.demo.common.dtos.ApiResponse;


@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "Store Analytics", description = "Endpoints for managing store analytics. All endpoints require ADMIN or MANAGER role.")
@SecurityRequirement(name = "bearerAuth")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    // ========== DASHBOARD ==========

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get dashboard data", description = "Returns the default store analytics dashboard (all-time or default period).")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Dashboard retrieved successfully",
                    content = @Content(schema = @Schema(implementation = DashboardResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {
        DashboardResponse response = analyticsService.getDashboardData();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/dashboard/filtered")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get dashboard data (filtered)", description = "Returns the store analytics dashboard filtered to the given date-time range.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Dashboard retrieved successfully",
                    content = @Content(schema = @Schema(implementation = DashboardResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid date range", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboardFiltered(
            @Parameter(description = "Start of the date range (ISO date-time)", required = true, example = "2026-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End of the date range (ISO date-time)", required = true, example = "2026-01-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        DashboardResponse response = analyticsService.getDashboardData(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ========== SALES REPORT ==========

    @GetMapping("/sales")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get sales report", description = "Returns a sales report for the given date range.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sales report retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SalesReportResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid date range", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<SalesReportResponse>> getSalesReport(
            @Parameter(description = "Start date (ISO date)", required = true, example = "2026-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (ISO date)", required = true, example = "2026-01-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        SalesReportResponse response = analyticsService.getSalesReport(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/sales/by-date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get sales report by date-time range", description = "Returns a sales report for the given date-time range, allowing finer-grained (sub-day) filtering than the date-only endpoint.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sales report retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SalesReportResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid date range", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<SalesReportResponse>> getSalesReportByDateRange(
            @Parameter(description = "Start of the date range (ISO date-time)", required = true, example = "2026-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End of the date range (ISO date-time)", required = true, example = "2026-01-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        SalesReportResponse response = analyticsService.getSalesReportByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ========== PRODUCT ANALYTICS ==========

    @GetMapping("/products/top")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get top-selling products", description = "Returns the top-selling products, limited to the given count.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Top-selling products retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<ProductAnalyticsResponse>>> getTopSellingProducts(
            @Parameter(description = "Maximum number of products to return", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        List<ProductAnalyticsResponse> response = analyticsService.getTopSellingProducts(limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/products/top-by-category/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get top-selling products by category", description = "Returns the top-selling products within the given category, limited to the given count.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Top-selling products retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<ProductAnalyticsResponse>>> getTopSellingProductsByCategory(
            @Parameter(description = "ID of the category", required = true)
            @PathVariable Long categoryId,
            @Parameter(description = "Maximum number of products to return", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        List<ProductAnalyticsResponse> response = analyticsService.getTopSellingProductsByCategory(categoryId, limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/products")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get product analytics", description = "Returns analytics data for all products.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product analytics retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<ProductAnalyticsResponse>>> getProductAnalytics() {
        List<ProductAnalyticsResponse> response = analyticsService.getProductAnalytics();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/products/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get product analytics by id", description = "Returns analytics data for the given product.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product analytics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProductAnalyticsResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<ProductAnalyticsResponse>> getProductAnalyticsById(
            @Parameter(description = "ID of the product", required = true)
            @PathVariable Long productId) {
        ProductAnalyticsResponse response = analyticsService.getProductAnalyticsById(productId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/products/low-performing")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get low-performing products", description = "Returns products whose sales fall at or below the given threshold.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Low-performing products retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<ProductAnalyticsResponse>>> getLowPerformingProducts(
            @Parameter(description = "Sales threshold below which a product is considered low-performing", example = "10")
            @RequestParam(defaultValue = "10") int threshold) {
        List<ProductAnalyticsResponse> response = analyticsService.getLowPerformingProducts(threshold);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ========== CATEGORY ANALYTICS ==========

    @GetMapping("/categories")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get category analytics", description = "Returns analytics data for all categories.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category analytics retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<CategoryAnalyticsResponse>>> getCategoryAnalytics() {
        List<CategoryAnalyticsResponse> response = analyticsService.getCategoryAnalytics();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/categories/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get category analytics by id", description = "Returns analytics data for the given category.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category analytics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CategoryAnalyticsResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<CategoryAnalyticsResponse>> getCategoryAnalyticsById(
            @Parameter(description = "ID of the category", required = true)
            @PathVariable Long categoryId) {
        CategoryAnalyticsResponse response = analyticsService.getCategoryAnalyticsById(categoryId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ========== GEOGRAPHIC REPORT ==========

    @GetMapping("/geographic")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get geographic report", description = "Returns a sales/orders breakdown across all geographic regions.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Geographic report retrieved successfully",
                    content = @Content(schema = @Schema(implementation = GeographicReportResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<GeographicReportResponse>> getGeographicReport() {
        GeographicReportResponse response = analyticsService.getGeographicReport();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/geographic/{country}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get geographic report by country", description = "Returns a sales/orders breakdown for the given country.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Geographic report retrieved successfully",
                    content = @Content(schema = @Schema(implementation = GeographicReportResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No data found for country", content = @Content)
    })
    public ResponseEntity<ApiResponse<GeographicReportResponse>> getGeographicReportByCountry(
            @Parameter(description = "Country name or code to filter by", required = true, example = "US")
            @PathVariable String country) {
        GeographicReportResponse response = analyticsService.getGeographicReportByCountry(country);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ========== TIME SERIES ==========

    @GetMapping("/sales/daily")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get daily sales time series", description = "Returns sales totals broken down by day for the given date range.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Daily sales retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid date range", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<DashboardResponse.SalesByDayResponse>>> getSalesByDay(
            @Parameter(description = "Start date (ISO date)", required = true, example = "2026-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (ISO date)", required = true, example = "2026-01-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<DashboardResponse.SalesByDayResponse> response = analyticsService.getSalesByDay(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ========== CUSTOMER ANALYTICS ==========

    @GetMapping("/customers/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get new customer count", description = "Returns the number of new customers acquired within the given date-time range.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "New customer count retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid date range", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<Long>> getNewCustomers(
            @Parameter(description = "Start of the date range (ISO date-time)", required = true, example = "2026-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End of the date range (ISO date-time)", required = true, example = "2026-01-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Long count = analyticsService.getNewCustomersCount(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/customers/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get active customer count", description = "Returns the number of customers who were active (e.g. placed an order) within the given date-time range.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Active customer count retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid date range", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<Long>> getActiveCustomers(
            @Parameter(description = "Start of the date range (ISO date-time)", required = true, example = "2026-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End of the date range (ISO date-time)", required = true, example = "2026-01-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Long count = analyticsService.getActiveCustomersCount(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    // ========== ORDER ANALYTICS ==========

    @GetMapping("/orders/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get order count by status", description = "Returns the number of orders currently in the given status.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order count retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<Long>> getOrderStatusDistribution(
            @Parameter(description = "Order status to filter by", required = true, example = "SHIPPED")
            @PathVariable String status) {
        Long count = analyticsService.getOrderStatusDistribution(status);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/orders/average-value")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get average order value", description = "Returns the average order value for orders placed within the given date-time range.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Average order value retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid date range", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<BigDecimal>> getAverageOrderValue(
            @Parameter(description = "Start of the date range (ISO date-time)", required = true, example = "2026-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End of the date range (ISO date-time)", required = true, example = "2026-01-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        BigDecimal avg = analyticsService.getAverageOrderValueByPeriod(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(avg));
    }
}