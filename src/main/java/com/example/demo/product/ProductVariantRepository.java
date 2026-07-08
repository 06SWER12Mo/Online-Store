package com.example.demo.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    
    // Find variants by product ID
    List<ProductVariant> findByProductId(Long productId);
    
    // Find variant by ID and product ID
    Optional<ProductVariant> findByIdAndProductId(Long id, Long productId);
    
    // Delete all variants for a product
    @Modifying
    @Transactional
    @Query("DELETE FROM ProductVariant pv WHERE pv.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);
    
    // Check if SKU exists
    boolean existsBySku(String sku);
    
    // Check if SKU exists excluding a specific ID
    @Query("SELECT CASE WHEN COUNT(pv) > 0 THEN true ELSE false END FROM ProductVariant pv WHERE pv.sku = :sku AND pv.id != :id")
    boolean existsBySkuAndIdNot(@Param("sku") String sku, @Param("id") Long id);
    
    // Find variants by SKU
    Optional<ProductVariant> findBySku(String sku);
    
    // Find variants with low stock
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.stockQuantity <= :threshold AND pv.inStock = true")
    List<ProductVariant> findLowStockVariants(@Param("threshold") Integer threshold);
    
    // Update stock quantity
    @Modifying
    @Transactional
    @Query("UPDATE ProductVariant pv SET pv.stockQuantity = :quantity, pv.inStock = CASE WHEN :quantity > 0 THEN true ELSE false END WHERE pv.id = :id")
    void updateStock(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    // Decrement stock
    @Modifying
    @Transactional
    @Query("UPDATE ProductVariant pv SET pv.stockQuantity = pv.stockQuantity - :quantity, " +
           "pv.inStock = CASE WHEN (pv.stockQuantity - :quantity) > 0 THEN true ELSE false END " +
           "WHERE pv.id = :id AND pv.stockQuantity >= :quantity")
    int decrementStock(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    // Increment stock
    @Modifying
    @Transactional
    @Query("UPDATE ProductVariant pv SET pv.stockQuantity = pv.stockQuantity + :quantity, pv.inStock = true WHERE pv.id = :id")
    void incrementStock(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    // Find variants by product ID and in stock
    List<ProductVariant> findByProductIdAndInStockTrue(Long productId);
    
    // Count variants by product
    @Query("SELECT COUNT(pv) FROM ProductVariant pv WHERE pv.product.id = :productId")
    long countByProductId(@Param("productId") Long productId);
}