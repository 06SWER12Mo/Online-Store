package com.example.demo.analytics;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.analytics.dtos.CategoryAnalyticsResponse;
import com.example.demo.analytics.dtos.DashboardResponse;
import com.example.demo.analytics.dtos.GeographicReportResponse;
import com.example.demo.analytics.dtos.ProductAnalyticsResponse;
import com.example.demo.analytics.dtos.SalesReportResponse;

public interface AnalyticsService {

    // Dashboard
    DashboardResponse getDashboardData();

    DashboardResponse getDashboardData(LocalDateTime startDate, LocalDateTime endDate);

    // Sales Report
    SalesReportResponse getSalesReport(LocalDate startDate, LocalDate endDate);

    SalesReportResponse getSalesReportByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    // Product Analytics
    List<ProductAnalyticsResponse> getTopSellingProducts(int limit);

    List<ProductAnalyticsResponse> getTopSellingProductsByCategory(Long categoryId, int limit);

    List<ProductAnalyticsResponse> getProductAnalytics();

    ProductAnalyticsResponse getProductAnalyticsById(Long productId);

    List<ProductAnalyticsResponse> getLowPerformingProducts(int threshold);

    // Category Analytics
    List<CategoryAnalyticsResponse> getCategoryAnalytics();

    CategoryAnalyticsResponse getCategoryAnalyticsById(Long categoryId);

    // Geographic Report
    GeographicReportResponse getGeographicReport();

    GeographicReportResponse getGeographicReportByCountry(String country);

    // Time Series
    List<DashboardResponse.SalesByDayResponse> getSalesByDay(LocalDate startDate, LocalDate endDate);

    List<DashboardResponse.SalesByDayResponse> getSalesByMonth(int year);

    // Customer Analytics
    Long getNewCustomersCount(LocalDateTime startDate, LocalDateTime endDate);

    Long getActiveCustomersCount(LocalDateTime startDate, LocalDateTime endDate);

    // Order Analytics
    Long getOrderStatusDistribution(String status);

    BigDecimal getAverageOrderValueByPeriod(LocalDateTime startDate, LocalDateTime endDate);
}