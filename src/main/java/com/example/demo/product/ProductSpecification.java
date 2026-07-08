package com.example.demo.product;

import jakarta.persistence.*;

@Entity
@Table(name = "product_specifications", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_id", "name"})
})
public class ProductSpecification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    // FIXED: Changed from "value" to "spec_value" to avoid H2 reserved keyword
    @Column(name = "spec_value", nullable = false, length = 500)
    private String value;

    @Column(length = 100)
    private String unit;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Constructors
    public ProductSpecification() {}

    public ProductSpecification(String name, String value, Product product) {
        this.name = name;
        this.value = value;
        this.product = product;
    }

    public ProductSpecification(String name, String value, String unit, Product product) {
        this.name = name;
        this.value = value;
        this.unit = unit;
        this.product = product;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}