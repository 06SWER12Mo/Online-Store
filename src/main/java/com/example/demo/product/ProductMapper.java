package com.example.demo.product;

import com.example.demo.category.Category;
import com.example.demo.category.CategoryRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    private final CategoryRepository categoryRepository;

    public ProductMapper(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Product toEntity(ProductRequest request) {
        Product product = new Product();
        updateEntity(product, request);
        return product;
    }

    public void updateEntity(Product product, ProductRequest request) {
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

    // Image mapping
    public ProductImage toImageEntity(ProductImageRequest request, Product product) {
        ProductImage image = new ProductImage();
        image.setImageUrl(request.getImageUrl());
        image.setAltText(request.getAltText());
        image.setPrimary(request.getPrimary() != null && request.getPrimary());
        image.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        image.setProduct(product);
        return image;
    }

    public ProductImageResponse toImageResponse(ProductImage image) {
        return new ProductImageResponse(image);
    }

    public List<ProductImageResponse> toImageResponseList(List<ProductImage> images) {
        return images.stream()
                .map(this::toImageResponse)
                .collect(Collectors.toList());
    }

    // Specification mapping
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

    // Variant mapping
    public ProductVariant toVariantEntity(ProductVariantRequest request, Product product) {
        ProductVariant variant = new ProductVariant();
        variant.setName(request.getName());
        variant.setSku(request.getSku());
        variant.setPrice(request.getPrice());
        variant.setCompareAtPrice(request.getCompareAtPrice());
        variant.setStockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0);
        variant.setWeight(request.getWeight());
        variant.setImageUrl(request.getImageUrl());
        variant.setProduct(product);
        return variant;
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