package com.example.demo.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.product.dtos.ProductRequest;
import com.example.demo.product.dtos.ProductResponse;
import com.example.demo.product.dtos.ProductSearchRequest;
import com.example.demo.product.dtos.ProductSpecificationRequest;
import com.example.demo.product.dtos.ProductSummaryResponse;
import com.example.demo.product.dtos.ProductUpdateRequest;
import com.example.demo.product.dtos.ProductVariantRequest;
import com.example.demo.product.dtos.ProductVariantResponse;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    // ========== BASIC CRUD ==========
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductUpdateRequest request);
    void deleteProduct(Long id);
    ProductResponse getProductById(Long id);
    ProductSummaryResponse getProductSummaryById(Long id);
    Page<ProductResponse> getAllProducts(Pageable pageable);
    Page<ProductSummaryResponse> getAllProductSummaries(Pageable pageable);

    // ========== SEARCH ==========
    Page<ProductResponse> searchProducts(ProductSearchRequest searchRequest);
    Page<ProductResponse> searchProducts(String keyword, Pageable pageable);

    // ========== CATEGORY BASED ==========
    Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable);
    Page<ProductResponse> getActiveProductsByCategory(Long categoryId, Pageable pageable);

    // ========== FEATURED PRODUCTS ==========
    List<ProductResponse> getFeaturedProducts();
    Page<ProductResponse> getFeaturedProducts(Pageable pageable);

    // ========== IN STOCK ==========
    Page<ProductResponse> getInStockProducts(Pageable pageable);

    // ========== LOW STOCK ==========
    List<ProductResponse> getLowStockProducts();
    Page<ProductResponse> getLowStockProducts(Pageable pageable);

    // ========== PRICE RANGE ==========
    Page<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    // ========== DISCOUNTED PRODUCTS ==========
    Page<ProductResponse> getDiscountedProducts(Pageable pageable);

    // ========== TOP RATED ==========
    List<ProductResponse> getTopRatedProducts(Pageable pageable);

    // ========== NEW ARRIVALS ==========
    List<ProductResponse> getNewArrivals(Pageable pageable);

    // ========== BEST SELLERS ==========
    List<ProductResponse> getBestSellers(Pageable pageable);

    // ========== MOST VIEWED ==========
    List<ProductResponse> getMostViewed(Pageable pageable);

    // ========== STOCK MANAGEMENT ==========
    void updateStock(Long productId, Integer quantity);
    void decrementStock(Long productId, Integer quantity);
    void incrementStock(Long productId, Integer quantity);

    // ========== TOGGLE STATUS ==========
    void toggleActive(Long productId);
    void toggleFeatured(Long productId);

    // ========== INCREMENT VIEW COUNT ==========
    void incrementViewCount(Long productId);

    // ========== UPDATE RATING ==========
    void updateRating(Long productId, Double averageRating, Integer totalReviews);

    // ========== SPECIFICATION MANAGEMENT ==========
    void addSpecification(Long productId, ProductSpecificationRequest request);
    void updateSpecification(Long specificationId, ProductSpecificationRequest request);
    void removeSpecification(Long specificationId);

    // ========== VARIANT MANAGEMENT ==========
    ProductVariantResponse addVariant(Long productId, ProductVariantRequest request);
    ProductVariantResponse updateVariant(Long variantId, ProductVariantRequest request);
    void removeVariant(Long variantId);

    // ========== STATISTICS ==========
    long countTotalProducts();
    long countActiveProducts();
    long countProductsByCategory(Long categoryId);
}