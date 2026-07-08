package com.example.demo.common.exception;

import java.time.LocalDateTime;

public class ErrorDetails {

    private LocalDateTime timestamp;
    private String message;
    private String details;

    // Constructors
    public ErrorDetails() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorDetails(String message, String details) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.details = details;
    }

    public ErrorDetails(LocalDateTime timestamp, String message, String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }

    // Getters and Setters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    // Builder pattern (optional but useful)
    public static ErrorDetailsBuilder builder() {
        return new ErrorDetailsBuilder();
    }

    // Static factory methods
    public static ErrorDetails of(String message, String details) {
        return new ErrorDetails(message, details);
    }

    public static ErrorDetails of(LocalDateTime timestamp, String message, String details) {
        return new ErrorDetails(timestamp, message, details);
    }

    @Override
    public String toString() {
        return "ErrorDetails{" +
                "timestamp=" + timestamp +
                ", message='" + message + '\'' +
                ", details='" + details + '\'' +
                '}';
    }

    // Builder Class (Inner Static Class)
    public static class ErrorDetailsBuilder {
        private LocalDateTime timestamp;
        private String message;
        private String details;

        public ErrorDetailsBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ErrorDetailsBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ErrorDetailsBuilder details(String details) {
            this.details = details;
            return this;
        }

        public ErrorDetails build() {
            if (timestamp == null) {
                timestamp = LocalDateTime.now();
            }
            return new ErrorDetails(timestamp, message, details);
        }
    }
}