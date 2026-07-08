package com.example.demo.product;

import jakarta.validation.constraints.NotBlank;

public class ProductSpecificationRequest {

    @NotBlank(message = "Specification name is required")
    private String name;

    @NotBlank(message = "Specification value is required")
    private String value;

    private String unit;
    private Integer displayOrder = 0;

    // Constructors
    public ProductSpecificationRequest() {}

    // Getters and Setters
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