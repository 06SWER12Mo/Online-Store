package com.example.demo.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProductSpecificationRepository extends JpaRepository<ProductSpecification, Long> {

    List<ProductSpecification> findByProductId(Long productId);

    List<ProductSpecification> findByProductIdOrderByDisplayOrderAsc(Long productId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProductSpecification ps WHERE ps.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);

    @Query("SELECT ps FROM ProductSpecification ps WHERE ps.product.id = :productId AND ps.name = :name")
    List<ProductSpecification> findByProductIdAndName(@Param("productId") Long productId, @Param("name") String name);
}