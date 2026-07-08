package com.example.demo.category;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryRequest request, Category parentCategory) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setActive(request.getActive() != null ? request.getActive() : true);
        category.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        category.setParentCategory(parentCategory);
        return category;
    }

    public void updateEntity(Category category, CategoryRequest request, Category parentCategory) {
        if (request.getName() != null) {
            category.setName(request.getName());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
        if (request.getImageUrl() != null) {
            category.setImageUrl(request.getImageUrl());
        }
        if (request.getActive() != null) {
            category.setActive(request.getActive());
        }
        if (request.getDisplayOrder() != null) {
            category.setDisplayOrder(request.getDisplayOrder());
        }
        category.setParentCategory(parentCategory);
    }

    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category);
    }

    public List<CategoryResponse> toResponseList(List<Category> categories) {
        return categories.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> toResponseListWithLimitedDepth(List<Category> categories, int depth) {
        return categories.stream()
                .map(category -> toResponseWithLimitedDepth(category, depth))
                .collect(Collectors.toList());
    }

    private CategoryResponse toResponseWithLimitedDepth(Category category, int depth) {
        CategoryResponse response = new CategoryResponse(category);
        if (depth > 0 && category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {
            response.setSubCategories(
                    category.getSubCategories().stream()
                            .map(sub -> toResponseWithLimitedDepth(sub, depth - 1))
                            .collect(Collectors.toList())
            );
        } else {
            response.setSubCategories(null);
        }
        return response;
    }
}