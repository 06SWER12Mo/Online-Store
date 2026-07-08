package com.example.demo.common.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

public class OrderNumberGenerator {

    private static final AtomicLong counter = new AtomicLong(1);
    private static final String PREFIX = "ORD";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private OrderNumberGenerator() {
        // Private constructor to prevent instantiation
    }

    /**
     * Generate a unique order number with format: ORD-YYYYMMDD-XXXX-XXXXX
     * Includes timestamp hash for uniqueness even if counter resets
     */
    public static String generateOrderNumber() {
        String datePart = LocalDate.now().format(DATE_FORMATTER);
        String timeHash = Long.toHexString(System.currentTimeMillis()).substring(0, 4).toUpperCase();
        String sequencePart = String.format("%05d", counter.getAndIncrement());
        return PREFIX + "-" + datePart + "-" + timeHash + "-" + sequencePart;
    }

    /**
     * Generate order number with custom prefix
     */
    public static String generateOrderNumber(String customPrefix) {
        String datePart = LocalDate.now().format(DATE_FORMATTER);
        String timeHash = Long.toHexString(System.currentTimeMillis()).substring(0, 4).toUpperCase();
        String sequencePart = String.format("%05d", counter.getAndIncrement());
        return customPrefix + "-" + datePart + "-" + timeHash + "-" + sequencePart;
    }

    /**
     * Generate order number for specific store/region
     */
    public static String generateOrderNumber(String storeCode, String regionCode) {
        String datePart = LocalDate.now().format(DATE_FORMATTER);
        String timeHash = Long.toHexString(System.currentTimeMillis()).substring(0, 4).toUpperCase();
        String sequencePart = String.format("%05d", counter.getAndIncrement());
        return storeCode + "-" + regionCode + "-" + datePart + "-" + timeHash + "-" + sequencePart;
    }

    /**
     * Reset counter (useful for testing)
     */
    public static void resetCounter() {
        counter.set(1);
    }

    /**
     * Get current counter value (for testing/monitoring)
     */
    public static long getCurrentCounter() {
        return counter.get();
    }
}