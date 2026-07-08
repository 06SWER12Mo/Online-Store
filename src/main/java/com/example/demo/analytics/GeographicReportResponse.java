package com.example.demo.analytics;

import java.math.BigDecimal;
import java.util.List;

public class GeographicReportResponse {

    private List<CountryReport> countries;
    private List<CityReport> cities;
    private List<RegionReport> regions;
    private BigDecimal totalRevenue;
    private Long totalOrders;

    // Constructors
    public GeographicReportResponse() {}

    // Getters and Setters
    public List<CountryReport> getCountries() {
        return countries;
    }

    public void setCountries(List<CountryReport> countries) {
        this.countries = countries;
    }

    public List<CityReport> getCities() {
        return cities;
    }

    public void setCities(List<CityReport> cities) {
        this.cities = cities;
    }

    public List<RegionReport> getRegions() {
        return regions;
    }

    public void setRegions(List<RegionReport> regions) {
        this.regions = regions;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    // Inner classes
    public static class CountryReport {
        private String country;
        private Long orderCount;
        private BigDecimal revenue;
        private Long customerCount;
        private Double revenuePercentage;

        public CountryReport() {}

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }

        public Long getOrderCount() { return orderCount; }
        public void setOrderCount(Long orderCount) { this.orderCount = orderCount; }

        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }

        public Long getCustomerCount() { return customerCount; }
        public void setCustomerCount(Long customerCount) { this.customerCount = customerCount; }

        public Double getRevenuePercentage() { return revenuePercentage; }
        public void setRevenuePercentage(Double revenuePercentage) { this.revenuePercentage = revenuePercentage; }
    }

    public static class CityReport {
        private String city;
        private String country;
        private Long orderCount;
        private BigDecimal revenue;
        private Long customerCount;

        public CityReport() {}

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }

        public Long getOrderCount() { return orderCount; }
        public void setOrderCount(Long orderCount) { this.orderCount = orderCount; }

        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }

        public Long getCustomerCount() { return customerCount; }
        public void setCustomerCount(Long customerCount) { this.customerCount = customerCount; }
    }

    public static class RegionReport {
        private String region;
        private String country;
        private Long orderCount;
        private BigDecimal revenue;

        public RegionReport() {}

        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }

        public Long getOrderCount() { return orderCount; }
        public void setOrderCount(Long orderCount) { this.orderCount = orderCount; }

        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
    }
}