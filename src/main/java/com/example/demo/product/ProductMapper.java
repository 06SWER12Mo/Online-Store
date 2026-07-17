package com.example.demo.product;

import com.example.demo.category.Category;
import com.example.demo.category.CategoryRepository;
import com.example.demo.product.dtos.ProductRequest;
import com.example.demo.product.dtos.ProductResponse;
import com.example.demo.product.dtos.ProductSpecificationRequest;
import com.example.demo.product.dtos.ProductSpecificationResponse;
import com.example.demo.product.dtos.ProductSummaryResponse;
import com.example.demo.product.dtos.ProductUpdateRequest;
import com.example.demo.product.dtos.ProductVariantRequest;
import com.example.demo.product.dtos.ProductVariantResponse;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    private final CategoryRepository categoryRepository;

    public ProductMapper(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // ========== PRODUCT MAPPING ==========

    public Product toEntity(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setShortDescription(request.getShortDescription());
        product.setPrice(request.getPrice());
        product.setCostPrice(request.getCostPrice());
        product.setCompareAtPrice(request.getCompareAtPrice());
        product.setSku(request.getSku());
        product.setBarcode(request.getBarcode());
        product.setStockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0);
        product.setLowStockThreshold(request.getLowStockThreshold() != null ? request.getLowStockThreshold() : 5);
        product.setWeight(request.getWeight());
        product.setLength(request.getLength());
        product.setWidth(request.getWidth());
        product.setHeight(request.getHeight());
        product.setActive(request.getActive() != null ? request.getActive() : true);
        product.setFeatured(request.getFeatured() != null ? request.getFeatured() : false);
        product.setDigital(request.getDigital() != null ? request.getDigital() : false);
        product.setMetaTitle(request.getMetaTitle());
        product.setMetaDescription(request.getMetaDescription());
        product.setMetaKeywords(request.getMetaKeywords());
        product.setInStock(product.getStockQuantity() > 0);

        // Set category
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + request.getCategoryId()));
            product.setCategory(category);
        }

        return product;
    }

    public void updateEntity(Product product, ProductUpdateRequest request) {
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getShortDescription() != null) {
            product.setShortDescription(request.getShortDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getCostPrice() != null) {
            product.setCostPrice(request.getCostPrice());
        }
        if (request.getCompareAtPrice() != null) {
            product.setCompareAtPrice(request.getCompareAtPrice());
        }
        if (request.getSku() != null) {
            product.setSku(request.getSku());
        }
        if (request.getBarcode() != null) {
            product.setBarcode(request.getBarcode());
        }
        if (request.getStockQuantity() != null) {
            product.setStockQuantity(request.getStockQuantity());
            product.setInStock(request.getStockQuantity() > 0);
        }
        if (request.getLowStockThreshold() != null) {
            product.setLowStockThreshold(request.getLowStockThreshold());
        }
        if (request.getWeight() != null) {
            product.setWeight(request.getWeight());
        }
        if (request.getLength() != null) {
            product.setLength(request.getLength());
        }
        if (request.getWidth() != null) {
            product.setWidth(request.getWidth());
        }
        if (request.getHeight() != null) {
            product.setHeight(request.getHeight());
        }
        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }
        if (request.getFeatured() != null) {
            product.setFeatured(request.getFeatured());
        }
        if (request.getDigital() != null) {
            product.setDigital(request.getDigital());
        }
        if (request.getMetaTitle() != null) {
            product.setMetaTitle(request.getMetaTitle());
        }
        if (request.getMetaDescription() != null) {
            product.setMetaDescription(request.getMetaDescription());
        }
        if (request.getMetaKeywords() != null) {
            product.setMetaKeywords(request.getMetaKeywords());
        }

        // Set category
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + request.getCategoryId()));
            product.setCategory(category);
        }
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(product);
    }

    public ProductSummaryResponse toSummaryResponse(Product product) {
        return new ProductSummaryResponse(product);
    }

    public List<ProductResponse> toResponseList(List<Product> products) {
        return products.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ProductSummaryResponse> toSummaryResponseList(List<Product> products) {
        return products.stream()
                .map(this::toSummaryResponse)
                .collect(Collectors.toList());
    }

    // ========== SPECIFICATION MAPPING ==========

    public ProductSpecification toSpecificationEntity(ProductSpecificationRequest request, Product product) {
        ProductSpecification specification = new ProductSpecification();
        specification.setName(request.getName());
        specification.setValue(request.getValue());
        specification.setUnit(request.getUnit());
        specification.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        specification.setProduct(product);
        return specification;
    }

    public ProductSpecificationResponse toSpecificationResponse(ProductSpecification specification) {
        return new ProductSpecificationResponse(specification);
    }

    public List<ProductSpecificationResponse> toSpecificationResponseList(List<ProductSpecification> specifications) {
        return specifications.stream()
                .map(this::toSpecificationResponse)
                .collect(Collectors.toList());
    }

    // ========== VARIANT MAPPING ==========

    public ProductVariant toVariantEntity(ProductVariantRequest request, Product product) {
        ProductVariant variant = new ProductVariant();
        variant.setName(request.getName());
        variant.setSku(request.getSku());
        variant.setPrice(request.getPrice());
        variant.setCompareAtPrice(request.getCompareAtPrice());
        variant.setStockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0);
        variant.setWeight(request.getWeight());
        // ❌ DO NOT set imageUrl here - will be set by ImageService
        variant.setProduct(product);
        return variant;
    }

    public void updateVariantEntity(ProductVariant variant, ProductVariantRequest request) {
        if (request.getName() != null) {
            variant.setName(request.getName());
        }
        if (request.getSku() != null) {
            variant.setSku(request.getSku());
        }
        if (request.getPrice() != null) {
            variant.setPrice(request.getPrice());
        }
        if (request.getCompareAtPrice() != null) {
            variant.setCompareAtPrice(request.getCompareAtPrice());
        }
        if (request.getStockQuantity() != null) {
            variant.setStockQuantity(request.getStockQuantity());
        }
        if (request.getWeight() != null) {
            variant.setWeight(request.getWeight());
        }
        // ❌ DO NOT update imageUrl here - managed by ImageService
    }

    public ProductVariantResponse toVariantResponse(ProductVariant variant) {
        return new ProductVariantResponse(variant);
    }

    public List<ProductVariantResponse> toVariantResponseList(List<ProductVariant> variants) {
        return variants.stream()
                .map(this::toVariantResponse)
                .collect(Collectors.toList());
    }
}