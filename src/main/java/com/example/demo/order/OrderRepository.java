package com.example.demo.order;

import com.example.demo.payment.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByUserId(Long userId);

    Page<Order> findByUserId(Long userId, Pageable pageable);

    // ========== NEW METHODS FOR FILTERING BY STATUS ==========
    
    Page<Order> findByUserIdAndOrderStatus(Long userId, OrderStatus status, Pageable pageable);
    
    long countByUserId(Long userId);
    
    long countByUserIdAndOrderStatus(Long userId, OrderStatus status);
    
    List<Order> findTop5ByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<Order> findTopNByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.orderStatus = :status")
    List<Order> findByOrderStatus(@Param("status") OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.paymentStatus = :status")
    List<Order> findByPaymentStatus(@Param("status") PaymentStatus status);

    @Query("SELECT o FROM Order o WHERE o.trackingCode = :trackingCode")
    Optional<Order> findByTrackingCode(@Param("trackingCode") String trackingCode);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderStatus = :status")
    Long countByOrderStatus(@Param("status") OrderStatus status);

    // ========== ANALYTICS METHODS ==========

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderStatus = :status")
    Long countByStatus(@Param("status") String status);

    @Query("SELECT SUM(o.totalPrice) FROM Order o")
    BigDecimal getTotalRevenue();

    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.createdAt BETWEEN :start AND :end")
    BigDecimal getRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :start AND :end")
    List<Order> findByCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT p.id, p.name, SUM(oi.quantity) as total_sold, SUM(oi.unitPrice * oi.quantity) as total_revenue " +
           "FROM Order o " +
           "JOIN o.orderItems oi " +
           "JOIN oi.product p " +
           "WHERE o.orderStatus = 'DELIVERED' " +
           "GROUP BY p.id, p.name " +
           "ORDER BY total_sold DESC")
    List<Object[]> findTopSellingProducts(@Param("limit") int limit);

    @Query("SELECT p.id, p.name, SUM(oi.quantity) as total_sold, SUM(oi.unitPrice * oi.quantity) as total_revenue " +
           "FROM Order o " +
           "JOIN o.orderItems oi " +
           "JOIN oi.product p " +
           "WHERE o.orderStatus = 'DELIVERED' AND p.category.id = :categoryId " +
           "GROUP BY p.id, p.name " +
           "ORDER BY total_sold DESC")
    List<Object[]> findTopSellingProductsByCategory(@Param("categoryId") Long categoryId, @Param("limit") int limit);

    @Query("SELECT SUM(o.totalPrice) FROM Order o " +
           "JOIN o.orderItems oi " +
           "JOIN oi.product p " +
           "WHERE p.category.id = :categoryId AND o.orderStatus = 'DELIVERED'")
    BigDecimal getRevenueByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT SUM(oi.quantity) FROM Order o " +
           "JOIN o.orderItems oi " +
           "JOIN oi.product p " +
           "WHERE p.category.id = :categoryId AND o.orderStatus = 'DELIVERED'")
    Long getTotalSoldByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT c.id, c.name, SUM(o.totalPrice) as revenue, COUNT(p.id) as product_count " +
           "FROM Category c " +
           "LEFT JOIN Product p ON p.category.id = c.id " +
           "LEFT JOIN Order o ON o.id IN (SELECT o2.id FROM Order o2 JOIN o2.orderItems oi JOIN oi.product p2 WHERE p2.category.id = c.id AND o2.orderStatus = 'DELIVERED') " +
           "GROUP BY c.id, c.name " +
           "ORDER BY revenue DESC")
    List<Object[]> findTopCategories();

    @Query("SELECT COUNT(DISTINCT o.user.id) FROM Order o WHERE o.createdAt BETWEEN :start AND :end")
    Long countDistinctUsersByCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT AVG(o.totalPrice) FROM Order o WHERE o.createdAt BETWEEN :start AND :end")
    BigDecimal getAverageOrderValueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :start AND :end")
    Long countOrdersBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

   @Query("SELECT COUNT(o) FROM Order o WHERE o.orderStatus = :status")
    Long countByStatus(@Param("status") OrderStatus status);
}