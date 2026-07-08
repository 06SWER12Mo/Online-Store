package com.example.demo.common.exception;

public class ResourceNotFoundException extends RuntimeException {

    private ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException categoryById(String resourceName, Object id) {
        return new ResourceNotFoundException(
            String.format("%s with ID '%s' not found.", resourceName, id)
        );
    }

    public static ResourceNotFoundException categoryByName(String resourceName, String name) {
        return new ResourceNotFoundException(
            String.format("%s with name '%s' not found.", resourceName, name)
        );
    }

    public static ResourceNotFoundException paymentById(Long id) {
        return new ResourceNotFoundException(
            String.format("Payment with ID '%d' not found.", id)
        );
    }

    public static ResourceNotFoundException paymentByTransactionId(String transactionId) {
        return new ResourceNotFoundException(
            String.format("Payment with Transaction ID '%s' not found.", transactionId)
        );
    }

    public static ResourceNotFoundException paymentByOrderId(Long orderId) {
        return new ResourceNotFoundException(
            String.format("Payment for Order ID '%d' not found.", orderId)
        );
    }

    public static ResourceNotFoundException userById(Long id) {
        return new ResourceNotFoundException(
            String.format("User with ID '%d' not found.", id)
        );
    }

    public static ResourceNotFoundException userByEmail(String email) {
        return new ResourceNotFoundException(
            String.format("User with email '%s' not found.", email)
        );
    }

    public static ResourceNotFoundException orderById(Long id) {
        return new ResourceNotFoundException(
            String.format("Order with ID '%d' not found.", id)
        );
    }

    public static ResourceNotFoundException ordersByUserId(Long userId) {
        return new ResourceNotFoundException(
            String.format("No orders found for User ID '%d'.", userId)
        );
    }
    
    public static ResourceNotFoundException productById(Long id) {
        return new ResourceNotFoundException(
            String.format("Product with ID '%d' not found.", id)
        );
    }

    public static ResourceNotFoundException cartById(Long id) {
        return new ResourceNotFoundException(
            String.format("Cart with ID '%d' not found.", id)
        );
    }

    public static ResourceNotFoundException cartByUserId(Long userId) {
        return new ResourceNotFoundException(
            String.format("Cart for User ID '%d' not found.", userId)
        );
    }

    public static ResourceNotFoundException emptyCart(Long userId) {
        return new ResourceNotFoundException(
            String.format("Cart for User ID '%d' is empty.", userId)
        );
    }

    public static ResourceNotFoundException productByName(String name) {
    return new ResourceNotFoundException(
        String.format("Product with name '%s' not found.", name)
    );
}

public static ResourceNotFoundException productsByCategory(String categoryName) {
    return new ResourceNotFoundException(
        String.format("No products found in category '%s'.", categoryName)
    );
}

public static ResourceNotFoundException productOutOfStock(Long id) {
    return new ResourceNotFoundException(
        String.format("Product with ID '%d' is currently out of stock.", id)
    );
}
}