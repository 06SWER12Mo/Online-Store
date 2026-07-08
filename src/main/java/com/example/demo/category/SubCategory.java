package com.example.demo.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SubCategory {

    private Long id;

    @NotBlank(message = "Subcategory name is required")
    @Size(min = 2, max = 100, message = "Subcategory name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Size(max = 200, message = "Image URL must not exceed 200 characters")
    private String imageUrl;

    private Boolean active = true;

    private Integer displayOrder = 0;

    @NotNull(message = "Parent category ID is required")
    private Long parentCategoryId;

    private String parentCategoryName;

    // Constructors
    public SubCategory() {}

    public SubCategory(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.description = category.getDescription();
        this.imageUrl = category.getImageUrl();
        this.active = category.isActive();
        this.displayOrder = category.getDisplayOrder();
        if (category.getParentCategory() != null) {
            this.parentCategoryId = category.getParentCategory().getId();
            this.parentCategoryName = category.getParentCategory().getName();
        }
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Long getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Long parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public String getParentCategoryName() {
        return parentCategoryName;
    }

    public void setParentCategoryName(String parentCategoryName) {
        this.parentCategoryName = parentCategoryName;
    }

    public Category toCategory(Category parentCategory) {
        Category category = new Category();
        category.setId(this.id);
        category.setName(this.name);
        category.setDescription(this.description);
        category.setImageUrl(this.imageUrl);
        category.setActive(this.active != null ? this.active : true);
        category.setDisplayOrder(this.displayOrder != null ? this.displayOrder : 0);
        category.setParentCategory(parentCategory);
        return category;
    }
}