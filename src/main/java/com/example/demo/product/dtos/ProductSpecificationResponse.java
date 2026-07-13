package com.example.demo.product.dtos;

import com.example.demo.product.ProductSpecification;

public class ProductSpecificationResponse {

    private Long id;
    private String name;
    private String value;
    private String unit;
    private Integer displayOrder;

    // Constructors
    public ProductSpecificationResponse() {}

    public ProductSpecificationResponse(ProductSpecification specification) {
        this.id = specification.getId();
        this.name = specification.getName();
        this.value = specification.getValue();
        this.unit = specification.getUnit();
        this.displayOrder = specification.getDisplayOrder();
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
}