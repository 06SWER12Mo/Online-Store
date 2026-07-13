package com.example.demo.receipt;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.receipt.dtos.ReceiptRequest;
import com.example.demo.receipt.dtos.ReceiptResponse;
import com.example.demo.receipt.dtos.SupplierRequest;
import com.example.demo.receipt.dtos.SupplierResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ReceiptService {

    // ========== RECEIPT OPERATIONS ==========
    
    ReceiptResponse createReceipt(ReceiptRequest request, Long userId);
    
    ReceiptResponse getReceiptById(Long id);
    
    ReceiptResponse getReceiptByNumber(String receiptNumber);
    
    Page<ReceiptResponse> getAllReceipts(Pageable pageable);
    
    Page<ReceiptResponse> getReceiptsBySupplier(Long supplierId, Pageable pageable);
    
    Page<ReceiptResponse> getReceiptsByStatus(String status, Pageable pageable);
    
    ReceiptResponse updateReceiptStatus(Long id, String status);
    
    ReceiptResponse updatePaymentStatus(Long id, String paymentStatus);
    
    ReceiptResponse approveReceipt(Long id, Long userId);
    
    void deleteReceipt(Long id);

    // ========== RECEIPT STATISTICS ==========
    
    BigDecimal getTotalReceiptsAmountBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    long countApprovedReceiptsBetween(LocalDateTime startDate, LocalDateTime endDate);

    // ========== SUPPLIER OPERATIONS ==========
    
    SupplierResponse createSupplier(SupplierRequest request);
    
    SupplierResponse updateSupplier(Long id, SupplierRequest request);
    
    void deleteSupplier(Long id);
    
    SupplierResponse getSupplierById(Long id);
    
    Page<SupplierResponse> getAllSuppliers(Pageable pageable);
    
    List<SupplierResponse> searchSuppliers(String keyword);
    
    void toggleSupplierActive(Long id);
}