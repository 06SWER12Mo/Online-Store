package com.example.demo.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySku(String sku);

    Optional<Product> findByBarcode(String barcode);

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Long id);

    boolean existsByBarcode(String barcode);

    // Find by category
    List<Product> findByCategoryId(Long categoryId);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    List<Product> findByCategoryIdAndActiveTrue(Long categoryId);

    Page<Product> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);

    // Find active products
    List<Product> findByActiveTrue();

    Page<Product> findByActiveTrue(Pageable pageable);

    // Find featured products
    List<Product> findByFeaturedTrue();

    Page<Product> findByFeaturedTrue(Pageable pageable);

    List<Product> findByFeaturedTrueAndActiveTrue();

    Page<Product> findByFeaturedTrueAndActiveTrue(Pageable pageable);

    // Find in stock
    List<Product> findByInStockTrue();

    Page<Product> findByInStockTrue(Pageable pageable);

    // Find low stock products
    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= p.lowStockThreshold AND p.active = true")
    List<Product> findLowStockProducts();

    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= p.lowStockThreshold AND p.active = true")
    Page<Product> findLowStockProducts(Pageable pageable);

    // ========== ANALYTICS METHODS ==========

    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity <= :threshold AND p.active = true")
    Long countLowStockProducts(@Param("threshold") int threshold);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity = :quantity")
    Long countByStockQuantity(@Param("quantity") int quantity);

    @Query("SELECT SUM(p.stockQuantity) FROM Product p")
    Long getTotalStockQuantity();

    @Query("SELECT MIN(p.price) FROM Product p WHERE p.category.id = :categoryId")
    BigDecimal findMinPriceByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT MAX(p.price) FROM Product p WHERE p.category.id = :categoryId")
    BigDecimal findMaxPriceByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT AVG(p.price) FROM Product p WHERE p.category.id = :categoryId")
    Double findAvgPriceByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.soldCount < :threshold")
    List<Product> findBySoldCountLessThan(@Param("threshold") int threshold);

    // Search by name or description
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.shortDescription) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProducts(@Param("keyword") String keyword);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.shortDescription) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);

    // Advanced search with filters
    @Query("SELECT p FROM Product p WHERE " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:active IS NULL OR p.active = :active) AND " +
           "(:inStock IS NULL OR p.inStock = :inStock) AND " +
           "(:featured IS NULL OR p.featured = :featured)")
    Page<Product> advancedSearch(
            @Param("name") String name,
            @Param("categoryId") Long categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("active") Boolean active,
            @Param("inStock") Boolean inStock,
            @Param("featured") Boolean featured,
            Pageable pageable);

    // Get products by price range
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    // FIXED: Get products with discount (compareAtPrice > price) - both methods use @Query
    @Query("SELECT p FROM Product p WHERE p.compareAtPrice IS NOT NULL AND p.compareAtPrice > p.price")
    List<Product> findProductsWithDiscount();

    @Query("SELECT p FROM Product p WHERE p.compareAtPrice IS NOT NULL AND p.compareAtPrice > p.price")
    Page<Product> findProductsWithDiscount(Pageable pageable);

    // Get products with average rating
    @Query("SELECT p FROM Product p WHERE p.averageRating >= :minRating ORDER BY p.averageRating DESC")
    List<Product> findByAverageRatingGreaterThanEqual(@Param("minRating") Double minRating);

    // Update stock
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.stockQuantity = :quantity, p.inStock = CASE WHEN :quantity > 0 THEN true ELSE false END WHERE p.id = :id")
    void updateStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    // Decrement stock
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity - :quantity, " +
           "p.inStock = CASE WHEN (p.stockQuantity - :quantity) > 0 THEN true ELSE false END " +
           "WHERE p.id = :id AND p.stockQuantity >= :quantity")
    int decrementStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    // Increment stock
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity + :quantity, p.inStock = true WHERE p.id = :id")
    void incrementStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    // Increment view count
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);

    // Increment sold count
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.soldCount = p.soldCount + :quantity WHERE p.id = :id")
    void incrementSoldCount(@Param("id") Long id, @Param("quantity") Integer quantity);

    // Update average rating
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.averageRating = :averageRating, p.totalReviews = :totalReviews WHERE p.id = :id")
    void updateRating(@Param("id") Long id, @Param("averageRating") Double averageRating, @Param("totalReviews") Integer totalReviews);

    // Toggle active status
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.active = :active WHERE p.id = :id")
    void updateActiveStatus(@Param("id") Long id, @Param("active") boolean active);

    // Toggle featured status
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.featured = :featured WHERE p.id = :id")
    void updateFeaturedStatus(@Param("id") Long id, @Param("featured") boolean featured);

    // Count products by category
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
    long countByCategoryId(@Param("categoryId") Long categoryId);

    // Count active products by category
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId AND p.active = true")
    long countActiveByCategoryId(@Param("categoryId") Long categoryId);

    // Get newest products
    @Query("SELECT p FROM Product p WHERE p.active = true ORDER BY p.createdAt DESC")
    List<Product> findNewestProducts(Pageable pageable);

    // Get most viewed products
    @Query("SELECT p FROM Product p WHERE p.active = true ORDER BY p.viewCount DESC")
    List<Product> findMostViewedProducts(Pageable pageable);

    // Get best selling products
    @Query("SELECT p FROM Product p WHERE p.active = true ORDER BY p.soldCount DESC")
    List<Product> findBestSellingProducts(Pageable pageable);

    // Get top rated products
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.totalReviews > 0 ORDER BY p.averageRating DESC")
    List<Product> findTopRatedProducts(Pageable pageable);

    // Find products by category IDs using @Query
    @Query("SELECT p FROM Product p WHERE p.category.id IN :categoryIds")
    List<Product> findByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

    @Query("SELECT p FROM Product p WHERE p.category.id IN :categoryIds")
    Page<Product> findByCategoryIds(@Param("categoryIds") List<Long> categoryIds, Pageable pageable);
}