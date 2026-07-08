package com.example.demo.product;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ========== Admin/Manager Endpoints ==========

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request) {
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> toggleActive(@PathVariable Long id) {
        productService.toggleActive(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/toggle-featured")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> toggleFeatured(@PathVariable Long id) {
        productService.toggleFeatured(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        productService.updateStock(id, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/increment-stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> incrementStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        productService.incrementStock(id, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/decrement-stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> decrementStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        productService.decrementStock(id, quantity);
        return ResponseEntity.ok().build();
    }

    // ========== Image Management ==========

    @PostMapping("/{productId}/images")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ProductImageResponse> addProductImage(
            @PathVariable Long productId,
            @Valid @RequestBody ProductImageRequest request) {
        ProductImageResponse response = productService.addProductImage(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{productId}/images/{imageId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> removeProductImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        productService.removeProductImage(productId, imageId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{productId}/images/{imageId}/set-primary")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> setPrimaryImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        productService.setPrimaryImage(productId, imageId);
        return ResponseEntity.ok().build();
    }

    // ========== Specification Management ==========

    @PostMapping("/{productId}/specifications")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> addSpecification(
            @PathVariable Long productId,
            @Valid @RequestBody ProductSpecificationRequest request) {
        productService.addSpecification(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/specifications/{specificationId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> updateSpecification(
            @PathVariable Long specificationId,
            @Valid @RequestBody ProductSpecificationRequest request) {
        productService.updateSpecification(specificationId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/specifications/{specificationId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> removeSpecification(@PathVariable Long specificationId) {
        productService.removeSpecification(specificationId);
        return ResponseEntity.noContent().build();
    }

    // ========== Variant Management ==========

    @PostMapping("/{productId}/variants")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ProductVariantResponse> addVariant(
            @PathVariable Long productId,
            @Valid @RequestBody ProductVariantRequest request) {
        ProductVariantResponse response = productService.addVariant(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/variants/{variantId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ProductVariantResponse> updateVariant(
            @PathVariable Long variantId,
            @Valid @RequestBody ProductVariantRequest request) {
        ProductVariantResponse response = productService.updateVariant(variantId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/variants/{variantId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> removeVariant(@PathVariable Long variantId) {
        productService.removeVariant(variantId);
        return ResponseEntity.noContent().build();
    }

    // ========== Public Endpoints ==========

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        // Increment view count
        productService.incrementViewCount(id);
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<ProductSummaryResponse> getProductSummary(@PathVariable Long id) {
        ProductSummaryResponse response = productService.getProductSummaryById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductResponse> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/summaries")
    public ResponseEntity<Page<ProductSummaryResponse>> getAllProductSummaries(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductSummaryResponse> products = productService.getAllProductSummaries(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductResponse> products = productService.searchProducts(keyword, pageable);
        return ResponseEntity.ok(products);
    }

    @PostMapping("/search/advanced")
    public ResponseEntity<Page<ProductResponse>> advancedSearch(@RequestBody ProductSearchRequest searchRequest) {
        Page<ProductResponse> products = productService.searchProducts(searchRequest);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductResponse> products = productService.getProductsByCategory(categoryId, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{categoryId}/active")
    public ResponseEntity<Page<ProductResponse>> getActiveProductsByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductResponse> products = productService.getActiveProductsByCategory(categoryId, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/featured")
    public ResponseEntity<List<ProductResponse>> getFeaturedProducts() {
        List<ProductResponse> products = productService.getFeaturedProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/featured/paginated")
    public ResponseEntity<Page<ProductResponse>> getFeaturedProductsPaginated(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductResponse> products = productService.getFeaturedProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/in-stock")
    public ResponseEntity<Page<ProductResponse>> getInStockProducts(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductResponse> products = productService.getInStockProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<ProductResponse>> getLowStockProducts() {
        List<ProductResponse> products = productService.getLowStockProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/low-stock/paginated")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Page<ProductResponse>> getLowStockProductsPaginated(
            @PageableDefault(size = 20, sort = "stockQuantity", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ProductResponse> products = productService.getLowStockProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/price-range")
    public ResponseEntity<Page<ProductResponse>> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @PageableDefault(size = 20, sort = "price", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ProductResponse> products = productService.getProductsByPriceRange(minPrice, maxPrice, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/on-sale")
    public ResponseEntity<Page<ProductResponse>> getDiscountedProducts(
            @PageableDefault(size = 20, sort = "discountPercentage", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductResponse> products = productService.getDiscountedProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/top-rated")
    public ResponseEntity<List<ProductResponse>> getTopRatedProducts(
            @PageableDefault(size = 10, sort = "averageRating", direction = Sort.Direction.DESC) Pageable pageable) {
        List<ProductResponse> products = productService.getTopRatedProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/new-arrivals")
    public ResponseEntity<List<ProductResponse>> getNewArrivals(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        List<ProductResponse> products = productService.getNewArrivals(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/best-sellers")
    public ResponseEntity<List<ProductResponse>> getBestSellers(
            @PageableDefault(size = 10, sort = "soldCount", direction = Sort.Direction.DESC) Pageable pageable) {
        List<ProductResponse> products = productService.getBestSellers(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/most-viewed")
    public ResponseEntity<List<ProductResponse>> getMostViewed(
            @PageableDefault(size = 10, sort = "viewCount", direction = Sort.Direction.DESC) Pageable pageable) {
        List<ProductResponse> products = productService.getMostViewed(pageable);
        return ResponseEntity.ok(products);
    }

    // ========== Statistics ==========

    @GetMapping("/stats/count")
    public ResponseEntity<Long> countTotalProducts() {
        return ResponseEntity.ok(productService.countTotalProducts());
    }

    @GetMapping("/stats/count/active")
    public ResponseEntity<Long> countActiveProducts() {
        return ResponseEntity.ok(productService.countActiveProducts());
    }

    @GetMapping("/stats/count/category/{categoryId}")
    public ResponseEntity<Long> countProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.countProductsByCategory(categoryId));
    }
}