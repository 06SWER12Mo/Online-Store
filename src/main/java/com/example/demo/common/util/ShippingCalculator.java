package com.example.demo.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ShippingCalculator {

    private static final double BASE_SHIPPING_COST = 5.00;
    private static final double COST_PER_KG = 2.50;
    private static final double COST_PER_CUBIC_METER = 10.00;
    private static final double EXPRESS_MULTIPLIER = 1.5;
    private static final double FREE_SHIPPING_THRESHOLD = 50.00;

    private ShippingCalculator() {
        // Private constructor to prevent instantiation
    }

    /**
     * Calculate standard shipping cost based on weight (in kg)
     */
    public static BigDecimal calculateByWeight(double weightKg) {
        if (weightKg <= 0) {
            return BigDecimal.valueOf(BASE_SHIPPING_COST);
        }
        
        double cost = BASE_SHIPPING_COST + (weightKg * COST_PER_KG);
        return BigDecimal.valueOf(cost)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate shipping cost based on dimensions (L x W x H in meters)
     */
    public static BigDecimal calculateByDimensions(double length, double width, double height) {
        double volume = length * width * height;
        if (volume <= 0) {
            return BigDecimal.valueOf(BASE_SHIPPING_COST);
        }
        
        double cost = BASE_SHIPPING_COST + (volume * COST_PER_CUBIC_METER);
        return BigDecimal.valueOf(cost)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate shipping cost based on weight and dimensions (using volumetric weight)
     */
    public static BigDecimal calculateByWeightAndDimensions(double weightKg, double length, double width, double height) {
        double volume = length * width * height;
        // Volumetric weight: 1 cubic meter = 200 kg (common in shipping)
        double volumetricWeight = volume * 200;
        double effectiveWeight = Math.max(weightKg, volumetricWeight);
        
        return calculateByWeight(effectiveWeight);
    }

    /**
     * Calculate express shipping cost (faster delivery)
     */
    public static BigDecimal calculateExpressShipping(double weightKg) {
        BigDecimal standardCost = calculateByWeight(weightKg);
        return standardCost.multiply(BigDecimal.valueOf(EXPRESS_MULTIPLIER))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate shipping cost with distance factor
     */
    public static BigDecimal calculateWithDistance(double weightKg, double distanceKm) {
        BigDecimal baseCost = calculateByWeight(weightKg);
        double distanceFactor = 1.0 + (distanceKm / 1000.0); // 1% increase per 10km
        return baseCost.multiply(BigDecimal.valueOf(distanceFactor))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Check if order qualifies for free shipping
     */
    public static boolean qualifiesForFreeShipping(BigDecimal orderTotal) {
        return orderTotal.compareTo(BigDecimal.valueOf(FREE_SHIPPING_THRESHOLD)) >= 0;
    }

    /**
     * Calculate shipping with free shipping threshold
     */
    public static BigDecimal calculateWithFreeShipping(double weightKg, BigDecimal orderTotal) {
        if (qualifiesForFreeShipping(orderTotal)) {
            return BigDecimal.ZERO;
        }
        return calculateByWeight(weightKg);
    }

    /**
     * Get estimated delivery days based on shipping type and distance
     */
    public static int estimateDeliveryDays(String shippingType, double distanceKm) {
        int baseDays;
        
        switch (shippingType.toLowerCase()) {
            case "express":
                baseDays = 1;
                break;
            case "priority":
                baseDays = 2;
                break;
            case "international":
                baseDays = 5;
                break;
            default: // standard
                baseDays = 3;
        }
        
        // Add 1 day for every 500km
        int distanceDays = (int) Math.floor(distanceKm / 500);
        
        return baseDays + distanceDays;
    }
}