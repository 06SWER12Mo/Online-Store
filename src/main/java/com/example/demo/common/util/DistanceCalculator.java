package com.example.demo.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DistanceCalculator {

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double EARTH_RADIUS_MILES = 3959.0;

    private DistanceCalculator() {
        // Private constructor to prevent instantiation
    }

    /**
     * Calculate distance between two coordinates using Haversine formula (in KM)
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        double a = Math.pow(Math.sin(deltaLat / 2), 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.pow(Math.sin(deltaLon / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Calculate distance between two coordinates (in KM) with BigDecimal precision
     */
    public static BigDecimal calculateDistancePrecise(double lat1, double lon1, double lat2, double lon2) {
        double distance = calculateDistance(lat1, lon1, lat2, lon2);
        return BigDecimal.valueOf(distance)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate distance between two coordinates in miles
     */
    public static double calculateDistanceInMiles(double lat1, double lon1, double lat2, double lon2) {
        double distanceInKm = calculateDistance(lat1, lon1, lat2, lon2);
        return distanceInKm * (EARTH_RADIUS_MILES / EARTH_RADIUS_KM);
    }

    /**
     * Check if a location is within a certain radius (in KM)
     */
    public static boolean isWithinRadius(double lat1, double lon1, double lat2, double lon2, double radiusKm) {
        double distance = calculateDistance(lat1, lon1, lat2, lon2);
        return distance <= radiusKm;
    }

    /**
     * Calculate the estimated delivery time based on distance
     * @param distanceKm distance in kilometers
     * @return estimated delivery time in minutes
     */
    public static int estimateDeliveryTime(double distanceKm) {
        // Assuming average speed of 30 km/h in city
        double timeInHours = distanceKm / 30.0;
        // Add 30 minutes handling time
        double totalTimeInHours = timeInHours + 0.5;
        return (int) Math.ceil(totalTimeInHours * 60);
    }

    /**
     * Calculate shipping cost based on distance and weight
     * @param distanceKm distance in kilometers
     * @param weightKg weight in kilograms
     * @return calculated shipping cost
     */
    public static BigDecimal calculateShippingCost(double distanceKm, double weightKg) {
        // Base rate: $2.00 + $0.50 per km + $1.00 per kg
        double baseCost = 2.00;
        double distanceCost = distanceKm * 0.50;
        double weightCost = weightKg * 1.00;
        
        return BigDecimal.valueOf(baseCost + distanceCost + weightCost)
                .setScale(2, RoundingMode.HALF_UP);
    }
}