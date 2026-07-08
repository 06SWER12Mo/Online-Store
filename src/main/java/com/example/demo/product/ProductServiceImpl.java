package com.example.demo.product;

import com.example.demo.category.CategoryRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductSpecificationRepository productSpecificationRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              ProductImageRepository productImageRepository,
                              ProductSpecificationRepository productSpecificationRepository,
                              ProductVariantRepository productVariantRepository,
                              ProductMapper productMapper,
                              CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.productSpecificationRepository = productSpecificationRepository;
        this.productVariantRepository = productVariantRepository;
        this.productMapper = productMapper;
        this.categoryRepository = categoryRepository;
    }

    // ========== Basic CRUD ==========

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        // Check if SKU already exists
        if (productRepository.existsBySku(request.getSku())) {
            throw new RuntimeException("Product with SKU '" + request.getSku() + "' already exists");
        }

        Product product = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);

        // Handle images
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (ProductImageRequest imageRequest : request.getImages()) {
                ProductImage image = productMapper.toImageEntity(imageRequest, savedProduct);
                productImageRepository.save(image);
            }
        }

        // Handle specifications
        if (request.getSpecifications() != null && !request.getSpecifications().isEmpty()) {
            for (ProductSpecificationRequest specRequest : request.getSpecifications()) {
                ProductSpecification spec = productMapper.toSpecificationEntity(specRequest, savedProduct);
                productSpecificationRepository.save(spec);
            }
        }

        // Handle variants
        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            for (ProductVariantRequest variantRequest : request.getVariants()) {
                // Check if variant SKU already exists
                if (productVariantRepository.existsBySku(variantRequest.getSku())) {
                    throw new RuntimeException("Variant with SKU '" + variantRequest.getSku() + "' already exists");
                }
                ProductVariant variant = productMapper.toVariantEntity(variantRequest, savedProduct);
                productVariantRepository.save(variant);
            }
        }

        // Fetch complete product with all relations
        Product completeProduct = productRepository.findById(savedProduct.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return productMapper.toResponse(completeProduct);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
        Product product = findProductById(id);

        // Check if SKU is being changed and is not already taken
        if (request.getSku() != null && !request.getSku().equals(product.getSku())) {
            if (productRepository.existsBySkuAndIdNot(request.getSku(), id)) {
                throw new RuntimeException("Product with SKU '" + request.getSku() + "' already exists");
            }
        }

        productMapper.updateEntity(product, request);
        Product updatedProduct = productRepository.save(product);

        // Update images if provided
        if (request.getImages() != null) {
            // Clear existing images
            productImageRepository.deleteByProductId(id);

            // Add new images
            for (ProductImageRequest imageRequest : request.getImages()) {
                ProductImage image = productMapper.toImageEntity(imageRequest, updatedProduct);
                productImageRepository.save(image);
            }
        }

        // Update specifications if provided
        if (request.getSpecifications() != null) {
            // Clear existing specifications
            productSpecificationRepository.deleteByProductId(id);

            // Add new specifications
            for (ProductSpecificationRequest specRequest : request.getSpecifications()) {
                ProductSpecification spec = productMapper.toSpecificationEntity(specRequest, updatedProduct);
                productSpecificationRepository.save(spec);
            }
        }

        // Update variants if provided
        if (request.getVariants() != null) {
            // Clear existing variants
            productVariantRepository.deleteByProductId(id);

            // Add new variants
            for (ProductVariantRequest variantRequest : request.getVariants()) {
                // Check if variant SKU already exists
                if (productVariantRepository.existsBySku(variantRequest.getSku())) {
                    throw new RuntimeException("Variant with SKU '" + variantRequest.getSku() + "' already exists");
                }
                ProductVariant variant = productMapper.toVariantEntity(variantRequest, updatedProduct);
                productVariantRepository.save(variant);
            }
        }

        return productMapper.toResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = findProductById(id);
        productRepository.delete(product);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = findProductById(id);
        // Increment view count
        productRepository.incrementViewCount(id);
        return productMapper.toResponse(product);
    }

    @Override
    public ProductSummaryResponse getProductSummaryById(Long id) {
        Product product = findProductById(id);
        return productMapper.toSummaryResponse(product);
    }

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toResponse);
    }

    @Override
    public Page<ProductSummaryResponse> getAllProductSummaries(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toSummaryResponse);
    }

    // ========== Search ==========

    @Override
    public Page<ProductResponse> searchProducts(ProductSearchRequest searchRequest) {
        // Build specification
        Specification<Product> spec = buildSpecification(searchRequest);

        // Create sort
        Sort sort = createSort(searchRequest);

        // Create pageable
        Pageable pageable = PageRequest.of(
                searchRequest.getPage() != null ? searchRequest.getPage() : 0,
                searchRequest.getSize() != null ? searchRequest.getSize() : 20,
                sort
        );

        return productRepository.findAll(spec, pageable)
                .map(productMapper::toResponse);
    }

    @Override
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchProducts(keyword, pageable)
                .map(productMapper::toResponse);
    }

    // ========== Category based ==========

    @Override
    public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable)
                .map(productMapper::toResponse);
    }

    @Override
    public Page<ProductResponse> getActiveProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable)
                .map(productMapper::toResponse);
    }

    // ========== Featured products ==========

    @Override
    public List<ProductResponse> getFeaturedProducts() {
        return productRepository.findByFeaturedTrueAndActiveTrue()
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductResponse> getFeaturedProducts(Pageable pageable) {
        return productRepository.findByFeaturedTrueAndActiveTrue(pageable)
                .map(productMapper::toResponse);
    }

    // ========== In stock ==========

    @Override
    public Page<ProductResponse> getInStockProducts(Pageable pageable) {
        return productRepository.findByInStockTrue(pageable)
                .map(productMapper::toResponse);
    }

    // ========== Low stock ==========

    @Override
    public List<ProductResponse> getLowStockProducts() {
        return productRepository.findLowStockProducts()
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductResponse> getLowStockProducts(Pageable pageable) {
        return productRepository.findLowStockProducts(pageable)
                .map(productMapper::toResponse);
    }

    // ========== Price range ==========

    @Override
    public Page<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findByPriceBetween(minPrice, maxPrice, pageable)
                .map(productMapper::toResponse);
    }

    // ========== Discounted products ==========

    @Override
    public Page<ProductResponse> getDiscountedProducts(Pageable pageable) {
        return productRepository.findProductsWithDiscount(pageable)
                .map(productMapper::toResponse);
    }

    // ========== Top rated ==========

    @Override
    public List<ProductResponse> getTopRatedProducts(Pageable pageable) {
        return productRepository.findTopRatedProducts(pageable)
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ========== New arrivals ==========

    @Override
    public List<ProductResponse> getNewArrivals(Pageable pageable) {
        return productRepository.findNewestProducts(pageable)
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ========== Best sellers ==========

    @Override
    public List<ProductResponse> getBestSellers(Pageable pageable) {
        return productRepository.findBestSellingProducts(pageable)
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ========== Most viewed ==========

    @Override
    public List<ProductResponse> getMostViewed(Pageable pageable) {
        return productRepository.findMostViewedProducts(pageable)
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ========== Stock management ==========

    @Override
    public void updateStock(Long productId, Integer quantity) {
        productRepository.updateStock(productId, quantity);
    }

    @Override
    public void decrementStock(Long productId, Integer quantity) {
        int updated = productRepository.decrementStock(productId, quantity);
        if (updated == 0) {
            throw new RuntimeException("Insufficient stock for product ID: " + productId);
        }
    }

    @Override
    public void incrementStock(Long productId, Integer quantity) {
        productRepository.incrementStock(productId, quantity);
    }

    // ========== Toggle status ==========

    @Override
    public void toggleActive(Long productId) {
        Product product = findProductById(productId);
        product.setActive(!product.isActive());
        productRepository.save(product);
    }

    @Override
    public void toggleFeatured(Long productId) {
        Product product = findProductById(productId);
        product.setFeatured(!product.isFeatured());
        productRepository.save(product);
    }

    // ========== Increment view count ==========

    @Override
    public void incrementViewCount(Long productId) {
        productRepository.incrementViewCount(productId);
    }

    // ========== Update rating ==========

    @Override
    public void updateRating(Long productId, Double averageRating, Integer totalReviews) {
        productRepository.updateRating(productId, averageRating, totalReviews);
    }

    // ========== Image management ==========

    @Override
    public ProductImageResponse addProductImage(Long productId, ProductImageRequest request) {
        Product product = findProductById(productId);

        // If this image is set as primary, clear existing primary
        if (request.getPrimary() != null && request.getPrimary()) {
            productImageRepository.clearPrimaryImages(productId);
        }

        ProductImage image = productMapper.toImageEntity(request, product);
        ProductImage savedImage = productImageRepository.save(image);
        return productMapper.toImageResponse(savedImage);
    }

    @Override
    public void removeProductImage(Long productId, Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));

        if (!image.getProduct().getId().equals(productId)) {
            throw new RuntimeException("Image does not belong to this product");
        }

        productImageRepository.delete(image);
    }

    @Override
    public void setPrimaryImage(Long productId, Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));

        if (!image.getProduct().getId().equals(productId)) {
            throw new RuntimeException("Image does not belong to this product");
        }

        productImageRepository.clearPrimaryImages(productId);
        image.setPrimary(true);
        productImageRepository.save(image);
    }

    // ========== Specification management ==========

    @Override
    public void addSpecification(Long productId, ProductSpecificationRequest request) {
        Product product = findProductById(productId);
        ProductSpecification spec = productMapper.toSpecificationEntity(request, product);
        productSpecificationRepository.save(spec);
    }

    @Override
    public void updateSpecification(Long specificationId, ProductSpecificationRequest request) {
        ProductSpecification spec = productSpecificationRepository.findById(specificationId)
                .orElseThrow(() -> new RuntimeException("Specification not found with id: " + specificationId));

        spec.setName(request.getName());
        spec.setValue(request.getValue());
        spec.setUnit(request.getUnit());
        spec.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        productSpecificationRepository.save(spec);
    }

    @Override
    public void removeSpecification(Long specificationId) {
        productSpecificationRepository.deleteById(specificationId);
    }

    // ========== Variant management ==========

    @Override
    public ProductVariantResponse addVariant(Long productId, ProductVariantRequest request) {
        Product product = findProductById(productId);
        
        // Check if variant SKU already exists
        if (productVariantRepository.existsBySku(request.getSku())) {
            throw new RuntimeException("Variant with SKU '" + request.getSku() + "' already exists");
        }
        
        ProductVariant variant = productMapper.toVariantEntity(request, product);
        ProductVariant savedVariant = productVariantRepository.save(variant);
        return productMapper.toVariantResponse(savedVariant);
    }

    @Override
    public ProductVariantResponse updateVariant(Long variantId, ProductVariantRequest request) {
        // Find the variant directly from the repository
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found with id: " + variantId));

        // Check if SKU is being changed and is not already taken
        if (request.getSku() != null && !request.getSku().equals(variant.getSku())) {
            if (productVariantRepository.existsBySkuAndIdNot(request.getSku(), variantId)) {
                throw new RuntimeException("Variant with SKU '" + request.getSku() + "' already exists");
            }
        }

        // Update variant fields
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
        if (request.getImageUrl() != null) {
            variant.setImageUrl(request.getImageUrl());
        }

        ProductVariant updatedVariant = productVariantRepository.save(variant);
        return productMapper.toVariantResponse(updatedVariant);
    }

    @Override
    public void removeVariant(Long variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found with id: " + variantId));
        
        productVariantRepository.delete(variant);
    }

    // ========== Statistics ==========

    @Override
    public long countTotalProducts() {
        return productRepository.count();
    }

    @Override
    public long countActiveProducts() {
        return productRepository.findByActiveTrue().size();
    }

    @Override
    public long countProductsByCategory(Long categoryId) {
        return productRepository.countByCategoryId(categoryId);
    }

    // ========== Additional variant helper methods ==========

    public List<ProductVariantResponse> getVariantsByProduct(Long productId) {
        return productVariantRepository.findByProductId(productId)
                .stream()
                .map(productMapper::toVariantResponse)
                .collect(Collectors.toList());
    }

    public List<ProductVariantResponse> getVariantsInStock(Long productId) {
        return productVariantRepository.findByProductIdAndInStockTrue(productId)
                .stream()
                .map(productMapper::toVariantResponse)
                .collect(Collectors.toList());
    }

    public void updateVariantStock(Long variantId, Integer quantity) {
        productVariantRepository.updateStock(variantId, quantity);
    }

    public void decrementVariantStock(Long variantId, Integer quantity) {
        int updated = productVariantRepository.decrementStock(variantId, quantity);
        if (updated == 0) {
            throw new RuntimeException("Insufficient stock for variant ID: " + variantId);
        }
    }

    public void incrementVariantStock(Long variantId, Integer quantity) {
        productVariantRepository.incrementStock(variantId, quantity);
    }

    // ========== Helper Methods ==========

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    private Specification<Product> buildSpecification(ProductSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
                String keyword = "%" + request.getKeyword().toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")), keyword);
                Predicate descPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")), keyword);
                Predicate shortDescPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("shortDescription")), keyword);
                predicates.add(criteriaBuilder.or(namePredicate, descPredicate, shortDescPredicate));
            }

            if (request.getName() != null && !request.getName().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + request.getName().toLowerCase() + "%"));
            }

            if (request.getCategoryId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), request.getCategoryId()));
            }

            if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
                predicates.add(root.get("category").get("id").in(request.getCategoryIds()));
            }

            if (request.getMinPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), request.getMinPrice()));
            }

            if (request.getMaxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), request.getMaxPrice()));
            }

            if (request.getActive() != null) {
                predicates.add(criteriaBuilder.equal(root.get("active"), request.getActive()));
            }

            if (request.getInStock() != null) {
                predicates.add(criteriaBuilder.equal(root.get("inStock"), request.getInStock()));
            }

            if (request.getFeatured() != null) {
                predicates.add(criteriaBuilder.equal(root.get("featured"), request.getFeatured()));
            }

            if (request.getOnSale() != null && request.getOnSale()) {
                predicates.add(criteriaBuilder.isNotNull(root.get("compareAtPrice")));
                predicates.add(criteriaBuilder.greaterThan(root.get("compareAtPrice"), root.get("price")));
            }

            if (request.getMinRating() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("averageRating"), request.getMinRating()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Sort createSort(ProductSearchRequest request) {
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "createdAt";
        String direction = request.getSortDirection() != null ? request.getSortDirection() : "DESC";

        Sort sort = Sort.by(sortBy);
        if (direction.equalsIgnoreCase("DESC")) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }
        return sort;
    }
}