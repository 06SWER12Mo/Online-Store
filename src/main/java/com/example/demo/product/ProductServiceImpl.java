package com.example.demo.product;

import com.example.demo.category.CategoryRepository;
import com.example.demo.image.ImageService;
import com.example.demo.image.dtos.ImageResponse;
import com.example.demo.product.dtos.ProductRequest;
import com.example.demo.product.dtos.ProductResponse;
import com.example.demo.product.dtos.ProductSearchRequest;
import com.example.demo.product.dtos.ProductSpecificationRequest;
import com.example.demo.product.dtos.ProductSummaryResponse;
import com.example.demo.product.dtos.ProductUpdateRequest;
import com.example.demo.product.dtos.ProductVariantRequest;
import com.example.demo.product.dtos.ProductVariantResponse;

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
    private final ProductSpecificationRepository productSpecificationRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    
    // ✅ ImageService for product images
    private final ImageService imageService;

    public ProductServiceImpl(ProductRepository productRepository,
                              ProductSpecificationRepository productSpecificationRepository,
                              ProductVariantRepository productVariantRepository,
                              ProductMapper productMapper,
                              CategoryRepository categoryRepository,
                              ImageService imageService) {
        this.productRepository = productRepository;
        this.productSpecificationRepository = productSpecificationRepository;
        this.productVariantRepository = productVariantRepository;
        this.productMapper = productMapper;
        this.categoryRepository = categoryRepository;
        this.imageService = imageService;
    }

    // ========== BASIC CRUD ==========

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new RuntimeException("Product with SKU '" + request.getSku() + "' already exists");
        }

        Product product = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);

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
                if (productVariantRepository.existsBySku(variantRequest.getSku())) {
                    throw new RuntimeException("Variant with SKU '" + variantRequest.getSku() + "' already exists");
                }
                ProductVariant variant = productMapper.toVariantEntity(variantRequest, savedProduct);
                productVariantRepository.save(variant);
            }
        }

        Product completeProduct = productRepository.findById(savedProduct.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        ProductResponse response = productMapper.toResponse(completeProduct);
        
        // ✅ Load images from ImageService
        loadProductImages(response, savedProduct.getId());
        
        return response;
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
        Product product = findProductById(id);

        if (request.getSku() != null && !request.getSku().equals(product.getSku())) {
            if (productRepository.existsBySkuAndIdNot(request.getSku(), id)) {
                throw new RuntimeException("Product with SKU '" + request.getSku() + "' already exists");
            }
        }

        productMapper.updateEntity(product, request);
        Product updatedProduct = productRepository.save(product);

        // Update specifications if provided
        if (request.getSpecifications() != null) {
            productSpecificationRepository.deleteByProductId(id);
            for (ProductSpecificationRequest specRequest : request.getSpecifications()) {
                ProductSpecification spec = productMapper.toSpecificationEntity(specRequest, updatedProduct);
                productSpecificationRepository.save(spec);
            }
        }

        // Update variants if provided
        if (request.getVariants() != null) {
            productVariantRepository.deleteByProductId(id);
            for (ProductVariantRequest variantRequest : request.getVariants()) {
                if (productVariantRepository.existsBySku(variantRequest.getSku())) {
                    throw new RuntimeException("Variant with SKU '" + variantRequest.getSku() + "' already exists");
                }
                ProductVariant variant = productMapper.toVariantEntity(variantRequest, updatedProduct);
                productVariantRepository.save(variant);
            }
        }

        ProductResponse response = productMapper.toResponse(updatedProduct);
        
        // ✅ Load images from ImageService
        loadProductImages(response, id);
        
        return response;
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = findProductById(id);
        
        // ✅ Delete all product images via ImageService
        imageService.deleteAllImages("product", id);
        
        productRepository.delete(product);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = findProductById(id);
        productRepository.incrementViewCount(id);
        
        ProductResponse response = productMapper.toResponse(product);
        
        // ✅ Load images from ImageService
        loadProductImages(response, id);
        
        return response;
    }

    @Override
    public ProductSummaryResponse getProductSummaryById(Long id) {
        Product product = findProductById(id);
        return productMapper.toSummaryResponse(product);
    }

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(product -> {
            ProductResponse response = productMapper.toResponse(product);
            loadProductImages(response, product.getId());
            return response;
        });
    }

    @Override
    public Page<ProductSummaryResponse> getAllProductSummaries(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toSummaryResponse);
    }

    // ========== SEARCH ==========

    @Override
    public Page<ProductResponse> searchProducts(ProductSearchRequest searchRequest) {
        Specification<Product> spec = buildSpecification(searchRequest);
        Sort sort = createSort(searchRequest);
        Pageable pageable = PageRequest.of(
                searchRequest.getPage() != null ? searchRequest.getPage() : 0,
                searchRequest.getSize() != null ? searchRequest.getSize() : 20,
                sort
        );

        Page<Product> productPage = productRepository.findAll(spec, pageable);
        return productPage.map(product -> {
            ProductResponse response = productMapper.toResponse(product);
            loadProductImages(response, product.getId());
            return response;
        });
    }

    @Override
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        Page<Product> productPage = productRepository.searchProducts(keyword, pageable);
        return productPage.map(product -> {
            ProductResponse response = productMapper.toResponse(product);
            loadProductImages(response, product.getId());
            return response;
        });
    }

    // ========== CATEGORY BASED ==========

    @Override
    public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        Page<Product> productPage = productRepository.findByCategoryId(categoryId, pageable);
        return productPage.map(product -> {
            ProductResponse response = productMapper.toResponse(product);
            loadProductImages(response, product.getId());
            return response;
        });
    }

    @Override
    public Page<ProductResponse> getActiveProductsByCategory(Long categoryId, Pageable pageable) {
        Page<Product> productPage = productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable);
        return productPage.map(product -> {
            ProductResponse response = productMapper.toResponse(product);
            loadProductImages(response, product.getId());
            return response;
        });
    }

    // ========== FEATURED PRODUCTS ==========

    @Override
    public List<ProductResponse> getFeaturedProducts() {
        return productRepository.findByFeaturedTrueAndActiveTrue()
                .stream()
                .map(product -> {
                    ProductResponse response = productMapper.toResponse(product);
                    loadProductImages(response, product.getId());
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductResponse> getFeaturedProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findByFeaturedTrueAndActiveTrue(pageable);
        return productPage.map(product -> {
            ProductResponse response = productMapper.toResponse(product);
            loadProductImages(response, product.getId());
            return response;
        });
    }

    // ========== IN STOCK ==========

    @Override
    public Page<ProductResponse> getInStockProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findByInStockTrue(pageable);
        return productPage.map(product -> {
            ProductResponse response = productMapper.toResponse(product);
            loadProductImages(response, product.getId());
            return response;
        });
    }

    // ========== LOW STOCK ==========

    @Override
    public List<ProductResponse> getLowStockProducts() {
        return productRepository.findLowStockProducts()
                .stream()
                .map(product -> {
                    ProductResponse response = productMapper.toResponse(product);
                    loadProductImages(response, product.getId());
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductResponse> getLowStockProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findLowStockProducts(pageable);
        return productPage.map(product -> {
            ProductResponse response = productMapper.toResponse(product);
            loadProductImages(response, product.getId());
            return response;
        });
    }

    // ========== PRICE RANGE ==========

    @Override
    public Page<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Page<Product> productPage = productRepository.findByPriceBetween(minPrice, maxPrice, pageable);
        return productPage.map(product -> {
            ProductResponse response = productMapper.toResponse(product);
            loadProductImages(response, product.getId());
            return response;
        });
    }

    // ========== DISCOUNTED PRODUCTS ==========

    @Override
    public Page<ProductResponse> getDiscountedProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findProductsWithDiscount(pageable);
        return productPage.map(product -> {
            ProductResponse response = productMapper.toResponse(product);
            loadProductImages(response, product.getId());
            return response;
        });
    }

    // ========== TOP RATED ==========

    @Override
    public List<ProductResponse> getTopRatedProducts(Pageable pageable) {
        return productRepository.findTopRatedProducts(pageable)
                .stream()
                .map(product -> {
                    ProductResponse response = productMapper.toResponse(product);
                    loadProductImages(response, product.getId());
                    return response;
                })
                .collect(Collectors.toList());
    }

    // ========== NEW ARRIVALS ==========

    @Override
    public List<ProductResponse> getNewArrivals(Pageable pageable) {
        return productRepository.findNewestProducts(pageable)
                .stream()
                .map(product -> {
                    ProductResponse response = productMapper.toResponse(product);
                    loadProductImages(response, product.getId());
                    return response;
                })
                .collect(Collectors.toList());
    }

    // ========== BEST SELLERS ==========

    @Override
    public List<ProductResponse> getBestSellers(Pageable pageable) {
        return productRepository.findBestSellingProducts(pageable)
                .stream()
                .map(product -> {
                    ProductResponse response = productMapper.toResponse(product);
                    loadProductImages(response, product.getId());
                    return response;
                })
                .collect(Collectors.toList());
    }

    // ========== MOST VIEWED ==========

    @Override
    public List<ProductResponse> getMostViewed(Pageable pageable) {
        return productRepository.findMostViewedProducts(pageable)
                .stream()
                .map(product -> {
                    ProductResponse response = productMapper.toResponse(product);
                    loadProductImages(response, product.getId());
                    return response;
                })
                .collect(Collectors.toList());
    }

    // ========== STOCK MANAGEMENT ==========

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

    // ========== TOGGLE STATUS ==========

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

    // ========== INCREMENT VIEW COUNT ==========

    @Override
    public void incrementViewCount(Long productId) {
        productRepository.incrementViewCount(productId);
    }

    // ========== UPDATE RATING ==========

    @Override
    public void updateRating(Long productId, Double averageRating, Integer totalReviews) {
        productRepository.updateRating(productId, averageRating, totalReviews);
    }

    // ========== SPECIFICATION MANAGEMENT ==========

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

    // ========== VARIANT MANAGEMENT ==========

    @Override
    public ProductVariantResponse addVariant(Long productId, ProductVariantRequest request) {
        Product product = findProductById(productId);
        
        if (productVariantRepository.existsBySku(request.getSku())) {
            throw new RuntimeException("Variant with SKU '" + request.getSku() + "' already exists");
        }
        
        ProductVariant variant = productMapper.toVariantEntity(request, product);
        ProductVariant savedVariant = productVariantRepository.save(variant);
        return productMapper.toVariantResponse(savedVariant);
    }

    @Override
    public ProductVariantResponse updateVariant(Long variantId, ProductVariantRequest request) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found with id: " + variantId));

        if (request.getSku() != null && !request.getSku().equals(variant.getSku())) {
            if (productVariantRepository.existsBySkuAndIdNot(request.getSku(), variantId)) {
                throw new RuntimeException("Variant with SKU '" + request.getSku() + "' already exists");
            }
        }

        if (request.getName() != null) variant.setName(request.getName());
        if (request.getSku() != null) variant.setSku(request.getSku());
        if (request.getPrice() != null) variant.setPrice(request.getPrice());
        if (request.getCompareAtPrice() != null) variant.setCompareAtPrice(request.getCompareAtPrice());
        if (request.getStockQuantity() != null) variant.setStockQuantity(request.getStockQuantity());
        if (request.getWeight() != null) variant.setWeight(request.getWeight());
        if (request.getImageUrl() != null) variant.setImageUrl(request.getImageUrl());

        ProductVariant updatedVariant = productVariantRepository.save(variant);
        return productMapper.toVariantResponse(updatedVariant);
    }

    @Override
    public void removeVariant(Long variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found with id: " + variantId));
        
        // ✅ Delete variant image via ImageService
        imageService.deleteAllImages("variant", variantId);
        
        productVariantRepository.delete(variant);
    }

    // ========== STATISTICS ==========

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

    // ========== HELPER METHODS ==========

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    // ✅ Load images from ImageService
    private void loadProductImages(ProductResponse response, Long productId) {
        try {
            List<ImageResponse> images = imageService.getProductImages(productId);
            response.setImages(images);
            
            // Set primary image URL
            ImageResponse primary = imageService.getPrimaryImage("product", productId);
            if (primary != null) {
                response.setPrimaryImageUrl(primary.getImageUrl());
            } else if (!images.isEmpty()) {
                response.setPrimaryImageUrl(images.get(0).getImageUrl());
            }
        } catch (Exception e) {
            // If images fail to load, just continue without them
            response.setImages(new ArrayList<>());
        }
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