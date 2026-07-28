package com.example.demo.dto.response;

import java.util.List;

public class HomePageResponse {

    private List<CategoryResponse> categories;
    private List<ProductSummaryResponse> featuredProducts;
    private List<ProductSummaryResponse> newArrivals;
    private List<ProductSummaryResponse> bestSellers;
    private StoreStats stats;

    public HomePageResponse() {}

    public HomePageResponse(List<CategoryResponse> categories,
                            List<ProductSummaryResponse> featuredProducts,
                            List<ProductSummaryResponse> newArrivals,
                            List<ProductSummaryResponse> bestSellers,
                            StoreStats stats) {
        this.categories = categories;
        this.featuredProducts = featuredProducts;
        this.newArrivals = newArrivals;
        this.bestSellers = bestSellers;
        this.stats = stats;
    }

    // Getters and Setters
    public List<CategoryResponse> getCategories() { return categories; }
    public void setCategories(List<CategoryResponse> categories) { this.categories = categories; }

    public List<ProductSummaryResponse> getFeaturedProducts() { return featuredProducts; }
    public void setFeaturedProducts(List<ProductSummaryResponse> featuredProducts) { this.featuredProducts = featuredProducts; }

    public List<ProductSummaryResponse> getNewArrivals() { return newArrivals; }
    public void setNewArrivals(List<ProductSummaryResponse> newArrivals) { this.newArrivals = newArrivals; }

    public List<ProductSummaryResponse> getBestSellers() { return bestSellers; }
    public void setBestSellers(List<ProductSummaryResponse> bestSellers) { this.bestSellers = bestSellers; }

    public StoreStats getStats() { return stats; }
    public void setStats(StoreStats stats) { this.stats = stats; }

    // Nested Stats DTO
    public static class StoreStats {
        private long totalProducts;
        private long totalCategories;
        private long totalOrders;
        private long happyCustomers;

        public StoreStats() {}

        public StoreStats(long totalProducts, long totalCategories, long totalOrders, long happyCustomers) {
            this.totalProducts = totalProducts;
            this.totalCategories = totalCategories;
            this.totalOrders = totalOrders;
            this.happyCustomers = happyCustomers;
        }

        public long getTotalProducts() { return totalProducts; }
        public void setTotalProducts(long totalProducts) { this.totalProducts = totalProducts; }

        public long getTotalCategories() { return totalCategories; }
        public void setTotalCategories(long totalCategories) { this.totalCategories = totalCategories; }

        public long getTotalOrders() { return totalOrders; }
        public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }

        public long getHappyCustomers() { return happyCustomers; }
        public void setHappyCustomers(long happyCustomers) { this.happyCustomers = happyCustomers; }
    }
}
