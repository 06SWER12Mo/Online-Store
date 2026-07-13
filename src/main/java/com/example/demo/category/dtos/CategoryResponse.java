package com.example.demo.category.dtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.category.Category;

public class CategoryResponse {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private boolean active;
    private Integer displayOrder;
    private Long parentId;
    private String parentName;
    private List<CategoryResponse> subCategories;
    private int subCategoryCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public CategoryResponse() {}

    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.description = category.getDescription();
        this.imageUrl = category.getImageUrl();
        this.active = category.isActive();
        this.displayOrder = category.getDisplayOrder();
        this.createdAt = category.getCreatedAt();
        this.updatedAt = category.getUpdatedAt();

        if (category.getParentCategory() != null) {
            this.parentId = category.getParentCategory().getId();
            this.parentName = category.getParentCategory().getName();
        }

        this.subCategoryCount = category.getSubCategories().size();
        this.subCategories = category.getSubCategories().stream()
                .map(CategoryResponse::new)
                .collect(Collectors.toList());
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public List<CategoryResponse> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<CategoryResponse> subCategories) {
        this.subCategories = subCategories;
    }

    public int getSubCategoryCount() {
        return subCategoryCount;
    }

    public void setSubCategoryCount(int subCategoryCount) {
        this.subCategoryCount = subCategoryCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}