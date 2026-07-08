package com.example.demo.analytics;

import com.example.demo.common.ApiResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    // ========== DASHBOARD ==========

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {
        DashboardResponse response = analyticsService.getDashboardData();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/dashboard/filtered")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboardFiltered(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        DashboardResponse response = analyticsService.getDashboardData(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ========== SALES REPORT ==========

    @GetMapping("/sales")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<SalesReportResponse>> getSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        SalesReportResponse response = analyticsService.getSalesReport(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/sales/by-date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<SalesReportResponse>> getSalesReportByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        SalesReportResponse response = analyticsService.getSalesReportByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ========== PRODUCT ANALYTICS ==========

    @GetMapping("/products/top")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<ProductAnalyticsResponse>>> getTopSellingProducts(
            @RequestParam(defaultValue = "10") int limit) {
        List<ProductAnalyticsResponse> response = analyticsService.getTopSellingProducts(limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/products/top-by-category/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<ProductAnalyticsResponse>>> getTopSellingProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "10") int limit) {
        List<ProductAnalyticsResponse> response = analyticsService.getTopSellingProductsByCategory(categoryId, limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/products")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<ProductAnalyticsResponse>>> getProductAnalytics() {
        List<ProductAnalyticsResponse> response = analyticsService.getProductAnalytics();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/products/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<ProductAnalyticsResponse>> getProductAnalyticsById(@PathVariable Long productId) {
        ProductAnalyticsResponse response = analyticsService.getProductAnalyticsById(productId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/products/low-performing")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<ProductAnalyticsResponse>>> getLowPerformingProducts(
            @RequestParam(defaultValue = "10") int threshold) {
        List<ProductAnalyticsResponse> response = analyticsService.getLowPerformingProducts(threshold);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ========== CATEGORY ANALYTICS ==========

    @GetMapping("/categories")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<CategoryAnalyticsResponse>>> getCategoryAnalytics() {
        List<CategoryAnalyticsResponse> response = analyticsService.getCategoryAnalytics();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/categories/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<CategoryAnalyticsResponse>> getCategoryAnalyticsById(@PathVariable Long categoryId) {
        CategoryAnalyticsResponse response = analyticsService.getCategoryAnalyticsById(categoryId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ========== GEOGRAPHIC REPORT ==========

    @GetMapping("/geographic")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<GeographicReportResponse>> getGeographicReport() {
        GeographicReportResponse response = analyticsService.getGeographicReport();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/geographic/{country}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<GeographicReportResponse>> getGeographicReportByCountry(@PathVariable String country) {
        GeographicReportResponse response = analyticsService.getGeographicReportByCountry(country);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ========== TIME SERIES ==========

    @GetMapping("/sales/daily")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<DashboardResponse.SalesByDayResponse>>> getSalesByDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<DashboardResponse.SalesByDayResponse> response = analyticsService.getSalesByDay(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ========== CUSTOMER ANALYTICS ==========

    @GetMapping("/customers/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Long>> getNewCustomers(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Long count = analyticsService.getNewCustomersCount(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/customers/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Long>> getActiveCustomers(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Long count = analyticsService.getActiveCustomersCount(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    // ========== ORDER ANALYTICS ==========

    @GetMapping("/orders/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Long>> getOrderStatusDistribution(@PathVariable String status) {
        Long count = analyticsService.getOrderStatusDistribution(status);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/orders/average-value")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<BigDecimal>> getAverageOrderValue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        BigDecimal avg = analyticsService.getAverageOrderValueByPeriod(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(avg));
    }
}