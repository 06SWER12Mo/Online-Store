package com.example.demo.product;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    // Basic CRUD
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductUpdateRequest request);
    void deleteProduct(Long id);
    ProductResponse getProductById(Long id);
    ProductSummaryResponse getProductSummaryById(Long id);
    Page<ProductResponse> getAllProducts(Pageable pageable);
    Page<ProductSummaryResponse> getAllProductSummaries(Pageable pageable);

    // Search
    Page<ProductResponse> searchProducts(ProductSearchRequest searchRequest);
    Page<ProductResponse> searchProducts(String keyword, Pageable pageable);

    // Category based
    Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable);
    Page<ProductResponse> getActiveProductsByCategory(Long categoryId, Pageable pageable);

    // Featured products
    List<ProductResponse> getFeaturedProducts();
    Page<ProductResponse> getFeaturedProducts(Pageable pageable);

    // In stock
    Page<ProductResponse> getInStockProducts(Pageable pageable);

    // Low stock
    List<ProductResponse> getLowStockProducts();
    Page<ProductResponse> getLowStockProducts(Pageable pageable);

    // Price range
    Page<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    // Discounted products
    Page<ProductResponse> getDiscountedProducts(Pageable pageable);

    // Top rated
    List<ProductResponse> getTopRatedProducts(Pageable pageable);

    // New arrivals
    List<ProductResponse> getNewArrivals(Pageable pageable);

    // Best sellers
    List<ProductResponse> getBestSellers(Pageable pageable);

    // Most viewed
    List<ProductResponse> getMostViewed(Pageable pageable);

    // Stock management
    void updateStock(Long productId, Integer quantity);
    void decrementStock(Long productId, Integer quantity);
    void incrementStock(Long productId, Integer quantity);

    // Toggle status
    void toggleActive(Long productId);
    void toggleFeatured(Long productId);

    // Increment view count
    void incrementViewCount(Long productId);

    // Update rating
    void updateRating(Long productId, Double averageRating, Integer totalReviews);

    // Image management
    ProductImageResponse addProductImage(Long productId, ProductImageRequest request);
    void removeProductImage(Long productId, Long imageId);
    void setPrimaryImage(Long productId, Long imageId);

    // Specification management
    void addSpecification(Long productId, ProductSpecificationRequest request);
    void updateSpecification(Long specificationId, ProductSpecificationRequest request);
    void removeSpecification(Long specificationId);

    // Variant management
    ProductVariantResponse addVariant(Long productId, ProductVariantRequest request);
    ProductVariantResponse updateVariant(Long variantId, ProductVariantRequest request);
    void removeVariant(Long variantId);

    // Statistics
    long countTotalProducts();
    long countActiveProducts();
    long countProductsByCategory(Long categoryId);
}