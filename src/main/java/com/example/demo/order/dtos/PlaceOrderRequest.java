package com.example.demo.order.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class PlaceOrderRequest {

    @NotNull
    private Long userId;

    @NotBlank
    private String guestName;

    @NotBlank
    @Email
    private String guestEmail;

    @NotBlank
    private String guestPhone;

    @NotBlank
    private String shippingName;

    @NotBlank
    private String shippingPhone;

    @NotNull
    private Long shippingTownId;

    @NotBlank
    private String shippingStreet;

    @NotBlank
    private String shippingBuilding;

    private Double latitude;

    private Double longitude;

    @NotEmpty
    @Valid
    private List<OrderItemRequest> items;

    // Nested DTO for order items
    public static class OrderItemRequest {
        @NotNull
        private Long productId;

        @NotNull
        private Integer quantity;

        // Getters and Setters
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getGuestEmail() { return guestEmail; }
    public void setGuestEmail(String guestEmail) { this.guestEmail = guestEmail; }

    public String getGuestPhone() { return guestPhone; }
    public void setGuestPhone(String guestPhone) { this.guestPhone = guestPhone; }

    public String getShippingName() { return shippingName; }
    public void setShippingName(String shippingName) { this.shippingName = shippingName; }

    public String getShippingPhone() { return shippingPhone; }
    public void setShippingPhone(String shippingPhone) { this.shippingPhone = shippingPhone; }

    public Long getShippingTownId() { return shippingTownId; }
    public void setShippingTownId(Long shippingTownId) { this.shippingTownId = shippingTownId; }

    public String getShippingStreet() { return shippingStreet; }
    public void setShippingStreet(String shippingStreet) { this.shippingStreet = shippingStreet; }

    public String getShippingBuilding() { return shippingBuilding; }
    public void setShippingBuilding(String shippingBuilding) { this.shippingBuilding = shippingBuilding; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }
}