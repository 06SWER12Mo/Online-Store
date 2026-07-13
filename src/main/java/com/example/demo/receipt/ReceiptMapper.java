package com.example.demo.receipt;

import com.example.demo.product.Product;
import com.example.demo.product.ProductRepository;
import com.example.demo.receipt.dtos.ReceiptItemRequest;
import com.example.demo.receipt.dtos.ReceiptItemResponse;
import com.example.demo.receipt.dtos.ReceiptRequest;
import com.example.demo.receipt.dtos.ReceiptResponse;
import com.example.demo.receipt.dtos.SupplierResponse;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReceiptMapper {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;

    public ReceiptMapper(ProductRepository productRepository,
                         SupplierRepository supplierRepository,
                         UserRepository userRepository) {
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.userRepository = userRepository;
    }

    // ========== RECEIPT MAPPINGS ==========

    public Receipt toEntity(ReceiptRequest request, Long userId) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Receipt receipt = new Receipt();
        receipt.setReceiptDate(request.getReceiptDate() != null ? request.getReceiptDate() : LocalDateTime.now());
        receipt.setSupplier(supplier);
        receipt.setNotes(request.getNotes());
        receipt.setPaymentMethod(request.getPaymentMethod());
        receipt.setReceiptType(request.getReceiptType() != null ? request.getReceiptType() : "PURCHASE");
        receipt.setShippingCost(request.getShippingCost() != null ? request.getShippingCost() : java.math.BigDecimal.ZERO);
        receipt.setDiscountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : java.math.BigDecimal.ZERO);
        receipt.setStatus("PENDING");
        receipt.setPaymentStatus("UNPAID");
        receipt.setCreatedBy(user);

        return receipt;
    }

    public ReceiptItem toItemEntity(ReceiptItemRequest request, Receipt receipt) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ReceiptItem item = new ReceiptItem();
        item.setProduct(product);
        item.setQuantity(request.getQuantity());
        item.setUnitPrice(request.getUnitPrice());
        item.setDiscountPercent(request.getDiscountPercent() != null ? request.getDiscountPercent() : java.math.BigDecimal.ZERO);
        item.setTaxPercent(request.getTaxPercent() != null ? request.getTaxPercent() : java.math.BigDecimal.ZERO);
        item.setNotes(request.getNotes());
        item.setReceipt(receipt);
        item.calculateTotals();

        return item;
    }

    public ReceiptResponse toResponse(Receipt receipt) {
        ReceiptResponse response = new ReceiptResponse(receipt);
        return response;
    }

    public ReceiptItemResponse toItemResponse(ReceiptItem item) {
        return new ReceiptItemResponse(item);
    }

    public List<ReceiptResponse> toResponseList(List<Receipt> receipts) {
        return receipts.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ========== SUPPLIER MAPPINGS ==========

    public SupplierResponse toSupplierResponse(Supplier supplier) {
        SupplierResponse response = new SupplierResponse(supplier);
        return response;
    }

    public List<SupplierResponse> toSupplierResponseList(List<Supplier> suppliers) {
        return suppliers.stream()
                .map(this::toSupplierResponse)
                .collect(Collectors.toList());
    }
}