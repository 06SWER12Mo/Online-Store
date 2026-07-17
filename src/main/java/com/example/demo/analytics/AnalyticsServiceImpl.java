package com.example.demo.analytics;

import com.example.demo.analytics.dtos.CategoryAnalyticsResponse;
import com.example.demo.analytics.dtos.DashboardResponse;
import com.example.demo.analytics.dtos.GeographicReportResponse;
import com.example.demo.analytics.dtos.ProductAnalyticsResponse;
import com.example.demo.analytics.dtos.SalesReportResponse;
import com.example.demo.category.Category;
import com.example.demo.category.CategoryRepository;
import com.example.demo.image.ImageService;
import com.example.demo.image.dtos.ImageResponse;
import com.example.demo.order.Order;
import com.example.demo.order.OrderRepository;
import com.example.demo.order.OrderStatus;
import com.example.demo.product.Product;
import com.example.demo.product.ProductRepository;
import com.example.demo.review.ReviewRepository;
import com.example.demo.user.UserRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ImageService imageService;

    public AnalyticsServiceImpl(OrderRepository orderRepository,
                                ProductRepository productRepository,
                                CategoryRepository categoryRepository,
                                UserRepository userRepository,
                                ReviewRepository reviewRepository,
                                ImageService imageService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.imageService = imageService;
    }

    // ========== DASHBOARD ==========

    @Override
    public DashboardResponse getDashboardData() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime weekStart = now.minusDays(7);
        LocalDateTime monthStart = now.minusDays(30);

        DashboardResponse response = new DashboardResponse();

        // Basic counts
        response.setTotalOrders(orderRepository.count());
        response.setTotalProducts(productRepository.count());
        response.setTotalCustomers(userRepository.count());
        response.setTotalCategories(categoryRepository.count());
        response.setTotalReviews(reviewRepository.count());

        // Revenue metrics
        BigDecimal totalRevenue = orderRepository.getTotalRevenue();
        response.setTotalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        BigDecimal todayRevenue = orderRepository.getRevenueBetween(todayStart, now);
        response.setTodayRevenue(todayRevenue != null ? todayRevenue : BigDecimal.ZERO);

        BigDecimal weekRevenue = orderRepository.getRevenueBetween(weekStart, now);
        response.setThisWeekRevenue(weekRevenue != null ? weekRevenue : BigDecimal.ZERO);

        BigDecimal monthRevenue = orderRepository.getRevenueBetween(monthStart, now);
        response.setThisMonthRevenue(monthRevenue != null ? monthRevenue : BigDecimal.ZERO);

        // Average order value
        Long orderCount = orderRepository.count();
        if (orderCount > 0 && totalRevenue != null) {
            response.setAverageOrderValue(totalRevenue.divide(BigDecimal.valueOf(orderCount), 2, RoundingMode.HALF_UP));
        } else {
            response.setAverageOrderValue(BigDecimal.ZERO);
        }

        response.setPendingOrders(orderRepository.countByStatus(OrderStatus.PENDING_PAYMENT));
        response.setProcessingOrders(orderRepository.countByStatus(OrderStatus.PAID));
        response.setShippedOrders(orderRepository.countByStatus(OrderStatus.SHIPPED));
        response.setDeliveredOrders(orderRepository.countByStatus(OrderStatus.DELIVERED));
        response.setCancelledOrders(orderRepository.countByStatus(OrderStatus.CANCELLED));

        // Stock metrics
        response.setLowStockProducts(productRepository.countLowStockProducts(10));
        response.setOutOfStockProducts(productRepository.countByStockQuantity(0));
        response.setTotalStockQuantity(productRepository.getTotalStockQuantity());

        // Revenue growth
        LocalDateTime previousMonthStart = monthStart.minusDays(30);
        BigDecimal previousRevenue = orderRepository.getRevenueBetween(previousMonthStart, monthStart);
        BigDecimal currentRevenue = orderRepository.getRevenueBetween(monthStart, now);

        if (previousRevenue != null && previousRevenue.compareTo(BigDecimal.ZERO) > 0 && currentRevenue != null) {
            double growth = (currentRevenue.doubleValue() - previousRevenue.doubleValue()) / previousRevenue.doubleValue() * 100;
            response.setRevenueGrowthPercentage(growth);
        } else {
            response.setRevenueGrowthPercentage(0.0);
        }

        // Recent data
        response.setRecentSales(getSalesByDay(LocalDate.now().minusDays(7), LocalDate.now()));
        response.setTopSellingProducts(getTopSellingProductResponses(10));
        response.setTopCategories(getTopCategories(10));
        response.setLastUpdated(now);

        return response;
    }

    @Override
    public DashboardResponse getDashboardData(LocalDateTime startDate, LocalDateTime endDate) {
        return getDashboardData();
    }

    // ========== SALES REPORT ==========

    @Override
    public SalesReportResponse getSalesReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        SalesReportResponse response = new SalesReportResponse();
        response.setStartDate(startDate);
        response.setEndDate(endDate);

        List<Order> orders = orderRepository.findByCreatedAtBetween(start, end);

        BigDecimal totalRevenue = BigDecimal.ZERO;
        Long totalItems = 0L;
        BigDecimal totalShipping = BigDecimal.ZERO;

        for (Order order : orders) {
            totalRevenue = totalRevenue.add(order.getTotalPrice());
            totalShipping = totalShipping.add(order.getShippingCost() != null ? order.getShippingCost() : BigDecimal.ZERO);

            if (order.getOrderItems() != null) {
                for (var item : order.getOrderItems()) {
                    totalItems += item.getQuantity();
                }
            }
        }

        response.setTotalRevenue(totalRevenue);
        response.setTotalOrders((long) orders.size());
        response.setTotalItemsSold(totalItems);
        response.setTotalTax(BigDecimal.ZERO);
        response.setTotalShipping(totalShipping);
        response.setTotalDiscount(BigDecimal.ZERO);

        if (!orders.isEmpty()) {
            response.setAverageOrderValue(totalRevenue.divide(BigDecimal.valueOf(orders.size()), 2, RoundingMode.HALF_UP));
            response.setAverageItemsPerOrder(BigDecimal.valueOf((double) totalItems / orders.size()).setScale(2, RoundingMode.HALF_UP));
        } else {
            response.setAverageOrderValue(BigDecimal.ZERO);
            response.setAverageItemsPerOrder(BigDecimal.ZERO);
        }

        // Daily summary
        List<SalesReportResponse.DailySalesSummary> dailySummary = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            LocalDateTime dayStart = currentDate.atStartOfDay();
            LocalDateTime dayEnd = currentDate.atTime(23, 59, 59);

            List<Order> dayOrders = orderRepository.findByCreatedAtBetween(dayStart, dayEnd);
            BigDecimal dayRevenue = BigDecimal.ZERO;
            Long dayItems = 0L;

            for (Order order : dayOrders) {
                dayRevenue = dayRevenue.add(order.getTotalPrice());
                if (order.getOrderItems() != null) {
                    for (var item : order.getOrderItems()) {
                        dayItems += item.getQuantity();
                    }
                }
            }

            dailySummary.add(new SalesReportResponse.DailySalesSummary(
                    currentDate, dayRevenue, (long) dayOrders.size(), dayItems
            ));
            currentDate = currentDate.plusDays(1);
        }
        response.setDailySummary(dailySummary);

        response.setNetRevenue(totalRevenue);

        return response;
    }

    @Override
    public SalesReportResponse getSalesReportByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return getSalesReport(startDate.toLocalDate(), endDate.toLocalDate());
    }

    // ========== PRODUCT ANALYTICS ==========

    @Override
    public List<ProductAnalyticsResponse> getTopSellingProducts(int limit) {
        List<Object[]> results = orderRepository.findTopSellingProducts(limit);
        List<ProductAnalyticsResponse> responses = new ArrayList<>();

        if (results.isEmpty()) {
            return responses;
        }

        List<Long> productIds = new ArrayList<>();
        for (Object[] row : results) {
            productIds.add((Long) row[0]);
        }

        List<Product> products = productRepository.findAllById(productIds);
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        for (Object[] row : results) {
            Long productId = (Long) row[0];
            ProductAnalyticsResponse response = new ProductAnalyticsResponse();
            response.setProductId(productId);
            response.setProductName((String) row[1]);
            response.setTotalSold(((Number) row[2]).intValue());
            response.setTotalRevenue((BigDecimal) row[3]);

            Product product = productMap.get(productId);
            if (product != null) {
                response.setProductSku(product.getSku());
                response.setPrice(product.getPrice());
                response.setStockQuantity(product.getStockQuantity());
                response.setAverageRating(product.getAverageRating());
                response.setTotalReviews(product.getTotalReviews());

                if (product.getCategory() != null) {
                    response.setCategoryId(product.getCategory().getId());
                    response.setCategoryName(product.getCategory().getName());
                }

                setProductPrimaryImage(response, productId);
            }

            responses.add(response);
        }

        return responses;
    }

    @Override
    public List<ProductAnalyticsResponse> getTopSellingProductsByCategory(Long categoryId, int limit) {
        List<Object[]> results = orderRepository.findTopSellingProductsByCategory(categoryId, limit);
        List<ProductAnalyticsResponse> responses = new ArrayList<>();

        if (results.isEmpty()) {
            return responses;
        }

        List<Long> productIds = new ArrayList<>();
        for (Object[] row : results) {
            productIds.add((Long) row[0]);
        }

        List<Product> products = productRepository.findAllById(productIds);
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        for (Object[] row : results) {
            Long productId = (Long) row[0];
            ProductAnalyticsResponse response = new ProductAnalyticsResponse();
            response.setProductId(productId);
            response.setProductName((String) row[1]);
            response.setTotalSold(((Number) row[2]).intValue());
            response.setTotalRevenue((BigDecimal) row[3]);

            Product product = productMap.get(productId);
            if (product != null) {
                response.setProductSku(product.getSku());
                response.setPrice(product.getPrice());
                response.setStockQuantity(product.getStockQuantity());
                setProductPrimaryImage(response, productId);
            }

            responses.add(response);
        }

        return responses;
    }

    @Override
    public List<ProductAnalyticsResponse> getProductAnalytics() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::toProductAnalyticsResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductAnalyticsResponse getProductAnalyticsById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        return toProductAnalyticsResponse(product);
    }

    @Override
    public List<ProductAnalyticsResponse> getLowPerformingProducts(int threshold) {
        List<Product> products = productRepository.findBySoldCountLessThan(threshold);
        return products.stream()
                .map(this::toProductAnalyticsResponse)
                .collect(Collectors.toList());
    }

    // ========== CATEGORY ANALYTICS ==========

    @Override
    public List<CategoryAnalyticsResponse> getCategoryAnalytics() {
        List<Category> categories = categoryRepository.findAll();
        BigDecimal totalRevenue = orderRepository.getTotalRevenue();
        totalRevenue = totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
        
        BigDecimal finalTotalRevenue = totalRevenue;
        
        return categories.stream()
                .map(category -> toCategoryAnalyticsResponse(category, finalTotalRevenue))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryAnalyticsResponse getCategoryAnalyticsById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
        BigDecimal totalRevenue = orderRepository.getTotalRevenue();
        totalRevenue = totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
        return toCategoryAnalyticsResponse(category, totalRevenue);
    }

    // ========== GEOGRAPHIC REPORT ==========

    @Override
    public GeographicReportResponse getGeographicReport() {
        GeographicReportResponse response = new GeographicReportResponse();
        response.setTotalRevenue(orderRepository.getTotalRevenue());
        response.setTotalOrders(orderRepository.count());
        return response;
    }

    @Override
    public GeographicReportResponse getGeographicReportByCountry(String country) {
        GeographicReportResponse response = new GeographicReportResponse();
        return response;
    }

    // ========== TIME SERIES ==========

    @Override
    public List<DashboardResponse.SalesByDayResponse> getSalesByDay(LocalDate startDate, LocalDate endDate) {
        List<DashboardResponse.SalesByDayResponse> result = new ArrayList<>();

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            LocalDateTime dayStart = current.atStartOfDay();
            LocalDateTime dayEnd = current.atTime(23, 59, 59);

            List<Order> dayOrders = orderRepository.findByCreatedAtBetween(dayStart, dayEnd);
            BigDecimal dayRevenue = BigDecimal.ZERO;
            for (Order order : dayOrders) {
                dayRevenue = dayRevenue.add(order.getTotalPrice());
            }

            result.add(new DashboardResponse.SalesByDayResponse(
                    current.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    dayRevenue,
                    (long) dayOrders.size()
            ));
            current = current.plusDays(1);
        }

        return result;
    }

    @Override
    public List<DashboardResponse.SalesByDayResponse> getSalesByMonth(int year) {
        return new ArrayList<>();
    }

    // ========== CUSTOMER ANALYTICS ==========

    @Override
    public Long getNewCustomersCount(LocalDateTime startDate, LocalDateTime endDate) {
        return userRepository.countByCreatedAtBetween(startDate, endDate);
    }

    @Override
    public Long getActiveCustomersCount(LocalDateTime startDate, LocalDateTime endDate) {
        return userRepository.countActiveCustomersBetween(startDate, endDate);
    }

    // ========== ORDER ANALYTICS ==========

    @Override
    public Long getOrderStatusDistribution(String status) {
        // ✅ Fix: Convert String to OrderStatus enum
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            return orderRepository.countByStatus(orderStatus);
        } catch (IllegalArgumentException e) {
            return 0L;
        }
    }

    @Override
    public BigDecimal getAverageOrderValueByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderRepository.findByCreatedAtBetween(startDate, endDate);
        if (orders.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;
        for (Order order : orders) {
            total = total.add(order.getTotalPrice());
        }
        return total.divide(BigDecimal.valueOf(orders.size()), 2, RoundingMode.HALF_UP);
    }

    // ========== PRIVATE HELPER METHODS ==========

    private ProductAnalyticsResponse toProductAnalyticsResponse(Product product) {
        ProductAnalyticsResponse response = new ProductAnalyticsResponse();
        response.setProductId(product.getId());
        response.setProductName(product.getName());
        response.setProductSku(product.getSku());
        response.setPrice(product.getPrice());
        response.setStockQuantity(product.getStockQuantity());
        response.setTotalSold(product.getSoldCount());
        response.setAverageRating(product.getAverageRating());
        response.setTotalReviews(product.getTotalReviews());
        response.setViewCount(product.getViewCount());

        if (product.getViewCount() != null && product.getViewCount() > 0) {
            double conversionRate = (double) product.getSoldCount() / product.getViewCount() * 100;
            response.setConversionRate(BigDecimal.valueOf(conversionRate).setScale(2, RoundingMode.HALF_UP));
        } else {
            response.setConversionRate(BigDecimal.ZERO);
        }

        if (product.getCategory() != null) {
            response.setCategoryId(product.getCategory().getId());
            response.setCategoryName(product.getCategory().getName());
        }

        setProductPrimaryImage(response, product.getId());

        return response;
    }

    private void setProductPrimaryImage(ProductAnalyticsResponse response, Long productId) {
        try {
            ImageResponse primaryImage = imageService.getPrimaryImage("product", productId);
            if (primaryImage != null) {
                response.setPrimaryImageUrl(primaryImage.getImageUrl());
            } else {
                List<ImageResponse> images = imageService.getProductImages(productId);
                if (!images.isEmpty()) {
                    response.setPrimaryImageUrl(images.get(0).getImageUrl());
                }
            }
        } catch (Exception e) {
            response.setPrimaryImageUrl(null);
        }
    }

    private CategoryAnalyticsResponse toCategoryAnalyticsResponse(Category category, BigDecimal totalRevenue) {
        CategoryAnalyticsResponse response = new CategoryAnalyticsResponse();
        
        response.setCategoryId(category.getId());
        response.setCategoryName(category.getName());
        
        if (category.getParentCategory() != null) {
            response.setParentCategoryId(category.getParentCategory().getId());
            response.setParentCategoryName(category.getParentCategory().getName());
        }
        
        Long productCount = productRepository.countByCategoryId(category.getId());
        response.setProductCount(productCount != null ? productCount : 0L);
        
        Long activeProductCount = productRepository.countActiveByCategoryId(category.getId());
        response.setActiveProductCount(activeProductCount != null ? activeProductCount : 0L);
        
        response.setSubCategoryCount(category.getSubCategories() != null ? category.getSubCategories().size() : 0);
        
        BigDecimal categoryRevenue = orderRepository.getRevenueByCategoryId(category.getId());
        response.setTotalRevenue(categoryRevenue != null ? categoryRevenue : BigDecimal.ZERO);
        
        Long categorySold = orderRepository.getTotalSoldByCategoryId(category.getId());
        response.setTotalSold(categorySold != null ? categorySold : 0L);
        
        BigDecimal minPrice = productRepository.findMinPriceByCategoryId(category.getId());
        BigDecimal maxPrice = productRepository.findMaxPriceByCategoryId(category.getId());
        Double avgPrice = productRepository.findAvgPriceByCategoryId(category.getId());
        
        response.setMinPrice(minPrice != null ? minPrice : BigDecimal.ZERO);
        response.setMaxPrice(maxPrice != null ? maxPrice : BigDecimal.ZERO);
        response.setAveragePrice(avgPrice != null ? avgPrice : 0.0);
        
        if (totalRevenue != null && totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal revenue = response.getTotalRevenue();
            if (revenue != null && revenue.compareTo(BigDecimal.ZERO) > 0) {
                double percentage = revenue.doubleValue() / totalRevenue.doubleValue() * 100;
                response.setRevenuePercentage(percentage);
            } else {
                response.setRevenuePercentage(0.0);
            }
        } else {
            response.setRevenuePercentage(0.0);
        }
        
        return response;
    }

    private List<DashboardResponse.TopCategoryResponse> getTopCategories(int limit) {
        List<Object[]> results = orderRepository.findTopCategories();
        List<DashboardResponse.TopCategoryResponse> responses = new ArrayList<>();

        int count = 0;
        for (Object[] row : results) {
            if (count >= limit) break;
            DashboardResponse.TopCategoryResponse response = new DashboardResponse.TopCategoryResponse();
            response.setCategoryId((Long) row[0]);
            response.setCategoryName((String) row[1]);
            response.setRevenue((BigDecimal) row[2]);
            response.setProductCount((Long) row[3]);
            responses.add(response);
            count++;
        }

        return responses;
    }

    private List<DashboardResponse.TopProductResponse> getTopSellingProductResponses(int limit) {
        List<Object[]> results = orderRepository.findTopSellingProducts(limit);
        List<DashboardResponse.TopProductResponse> responses = new ArrayList<>();

        for (Object[] row : results) {
            DashboardResponse.TopProductResponse response = new DashboardResponse.TopProductResponse();
            response.setProductId((Long) row[0]);
            response.setProductName((String) row[1]);
            response.setTotalSold(((Number) row[2]).longValue());
            response.setRevenue((BigDecimal) row[3]);
            
            try {
                ImageResponse primaryImage = imageService.getPrimaryImage("product", (Long) row[0]);
                if (primaryImage != null) {
                    response.setImageUrl(primaryImage.getImageUrl());
                }
            } catch (Exception e) {
                response.setImageUrl(null);
            }
            
            responses.add(response);
        }

        return responses;
    }
}