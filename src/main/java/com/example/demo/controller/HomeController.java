package com.example.demo.controller;

import com.example.demo.common.dtos.ApiResponse;
import com.example.demo.dto.response.CategoryResponse;
import com.example.demo.dto.response.HomePageResponse;
import com.example.demo.dto.response.HomePageResponse.StoreStats;
import com.example.demo.dto.response.ProductSummaryResponse;
import com.example.demo.service.CategoryService;
import com.example.demo.service.impl.ProductServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/home")
@Tag(name = "Home Page", description = "Aggregated home page data for the store front")
public class HomeController {

    private final ProductServiceImpl productService;
    private final CategoryService categoryService;

    public HomeController(ProductServiceImpl productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(
        summary = "Get home page data",
        description = "Returns aggregated data for the store home page including categories, featured products, new arrivals, best sellers, and store stats. No authentication required."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Home page data retrieved successfully",
            content = @Content(schema = @Schema(implementation = HomePageResponse.class))
        )
    })
    public ResponseEntity<ApiResponse<HomePageResponse>> getHomePage() {
        // Fetch active root categories (top 6)
        List<CategoryResponse> categories = categoryService.getActiveRootCategories();
        if (categories.size() > 6) {
            categories = categories.subList(0, 6);
        }

        // Fetch featured products (top 8)
        List<ProductSummaryResponse> featuredProducts = productService.getFeaturedProducts()
                .stream()
                .map(p -> {
                    ProductSummaryResponse summary = new ProductSummaryResponse();
                    mapToSummary(p, summary);
                    return summary;
                })
                .limit(10)
                .toList();

        // Fetch new arrivals (top 8)
        List<ProductSummaryResponse> newArrivals = productService.getNewArrivals(PageRequest.of(0, 10))
                .stream()
                .map(p -> {
                    ProductSummaryResponse summary = new ProductSummaryResponse();
                    mapToSummary(p, summary);
                    return summary;
                })
                .toList();

        // Fetch best sellers (top 8)
        List<ProductSummaryResponse> bestSellers = productService.getBestSellers(PageRequest.of(0, 10))
                .stream()
                .map(p -> {
                    ProductSummaryResponse summary = new ProductSummaryResponse();
                    mapToSummary(p, summary);
                    return summary;
                })
                .toList();

        // Calculate stats
        long totalProducts = productService.getAllProductSummaries(PageRequest.of(0, 1))
                .getTotalElements();
        long totalCategories = categoryService.getActiveRootCategories().size();

        StoreStats stats = new StoreStats(
            totalProducts,
            totalCategories,
            15000L,   // Placeholder — replace with actual order count when OrderService is available
            8500L     // Placeholder — replace with actual user count when available
        );

        HomePageResponse response = new HomePageResponse(
            categories,
            featuredProducts,
            newArrivals,
            bestSellers,
            stats
        );

        return ResponseEntity.ok(ApiResponse.success("Home page data retrieved successfully", response));
    }

    /**
     * Manually map fields from ProductResponse to ProductSummaryResponse.
     * This avoids needing the full Product entity when the service returns ProductResponse.
     */
    private void mapToSummary(com.example.demo.dto.response.ProductResponse source,
                               ProductSummaryResponse target) {
        target.setId(source.getId());
        target.setName(source.getName());
        target.setShortDescription(source.getShortDescription());
        target.setPrice(source.getPrice());
        target.setDiscountedPrice(source.getDiscountedPrice());
        target.setDiscountPercentage(source.getDiscountPercentage());
        target.setSku(source.getSku());
        target.setCompareAtPrice(source.getCompareAtPrice());
        target.setStockQuantity(source.getStockQuantity());
        target.setInStock(source.isInStock());
        target.setActive(source.isActive());
        target.setFeatured(source.isFeatured());
        target.setAverageRating(source.getAverageRating());
        target.setTotalReviews(source.getTotalReviews());
        target.setCategoryId(source.getCategoryId());
        target.setCategoryName(source.getCategoryName());
        target.setPrimaryImageUrl(source.getPrimaryImageUrl());
        target.setViewCount(source.getViewCount());
        target.setSoldCount(source.getSoldCount());
    }
}
