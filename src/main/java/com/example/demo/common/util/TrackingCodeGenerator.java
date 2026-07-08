package com.example.demo.common.util;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TrackingCodeGenerator {

    private static final SecureRandom random = new SecureRandom();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;

    private TrackingCodeGenerator() {
        // Private constructor to prevent instantiation
    }

    /**
     * Generate a random tracking code with format: TRK-XXXXX
     */
    public static String generateTrackingCode() {
        String randomPart = generateRandomString(CODE_LENGTH);
        return "TRK-" + randomPart;
    }

    /**
     * Generate tracking code with date prefix: TRK-YYYYMMDD-XXXXX
     */
    public static String generateTrackingCodeWithDate() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = generateRandomString(CODE_LENGTH);
        return "TRK-" + datePart + "-" + randomPart;
    }

    /**
     * Generate tracking code with custom prefix
     */
    public static String generateTrackingCode(String prefix) {
        String randomPart = generateRandomString(CODE_LENGTH);
        return prefix + "-" + randomPart;
    }

    /**
     * Generate tracking code for specific carrier
     */
    public static String generateCarrierTrackingCode(String carrier) {
        String randomPart = generateRandomString(CODE_LENGTH);
        return carrier.toUpperCase() + "-" + randomPart;
    }

    /**
     * Generate international tracking code (USPS/FedEx style)
     */
    public static String generateInternationalTrackingCode() {
        String randomPart = generateRandomString(CODE_LENGTH);
        return "INTL-" + randomPart;
    }

    /**
     * Generate tracking code with checksum digit
     */
    public static String generateTrackingCodeWithChecksum() {
        String base = generateRandomString(CODE_LENGTH);
        char checksum = calculateChecksum(base);
        return base + checksum;
    }

    /**
     * Generate a random string of specified length
     */
    private static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    /**
     * Calculate a simple checksum digit
     */
    private static char calculateChecksum(String input) {
        int sum = 0;
        for (int i = 0; i < input.length(); i++) {
            sum += input.charAt(i);
        }
        int checksum = sum % 36;
        if (checksum < 10) {
            return (char) ('0' + checksum);
        } else {
            return (char) ('A' + checksum - 10);
        }
    }

    /**
     * Validate tracking code format
     */
    public static boolean isValidTrackingCode(String trackingCode) {
        if (trackingCode == null || trackingCode.isEmpty()) {
            return false;
        }
        
        // Check format: prefix + hyphen + alphanumeric
        String[] parts = trackingCode.split("-");
        if (parts.length != 2) {
            return false;
        }
        
        String prefix = parts[0];
        String code = parts[1];
        
        if (prefix.isEmpty() || code.isEmpty()) {
            return false;
        }
        
        // Check if code part contains only alphanumeric characters
        return code.matches("^[A-Z0-9]+$");
    }

    /**
     * Extract prefix from tracking code
     */
    public static String getPrefix(String trackingCode) {
        if (trackingCode == null || !trackingCode.contains("-")) {
            return null;
        }
        return trackingCode.split("-")[0];
    }

    /**
     * Generate a batch of tracking codes
     */
    public static String[] generateBatch(int count) {
        String[] codes = new String[count];
        for (int i = 0; i < count; i++) {
            codes[i] = generateTrackingCode();
        }
        return codes;
    }
}