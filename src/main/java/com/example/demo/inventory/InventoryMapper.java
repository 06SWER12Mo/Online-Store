package com.example.demo.inventory;

import org.springframework.stereotype.Component;

import com.example.demo.inventory.dtos.InventoryReportResponse;
import com.example.demo.inventory.dtos.InventoryTransactionResponse;
import com.example.demo.order.Order;
import com.example.demo.receipt.Receipt;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InventoryMapper {

    public InventoryTransactionResponse toInventoryTransactionResponse(InventoryTransaction transaction) {
        if (transaction == null) return null;

        InventoryTransactionResponse response = new InventoryTransactionResponse();
        response.setId(transaction.getId());
        response.setProductId(transaction.getProduct() != null ? transaction.getProduct().getId() : null);
        response.setProductName(transaction.getProduct() != null ? transaction.getProduct().getName() : null);
        response.setProductSku(transaction.getProduct() != null ? transaction.getProduct().getSku() : null);
        response.setTransactionType(transaction.getTransactionType());
        response.setQuantity(transaction.getQuantity());
        response.setReferenceId(transaction.getReferenceId());
        
        if (transaction.getCreatedByUser() != null) {
            response.setCreatedByUserId(transaction.getCreatedByUser().getId());
            response.setCreatedByUserName(
                transaction.getCreatedByUser().getFirstName() + " " + 
                transaction.getCreatedByUser().getLastName()
            );
        }
        
        response.setCreatedAt(transaction.getCreatedAt());
        response.setNotes(transaction.getNotes());

        return response;
    }

    public List<InventoryTransactionResponse> toInventoryTransactionResponseList(
            List<InventoryTransaction> transactions) {
        return transactions.stream()
            .map(this::toInventoryTransactionResponse)
            .collect(Collectors.toList());
    }

    public InventoryTransactionResponse toTransactionResponseFromAdjustment(StockAdjustment adjustment) {
        if (adjustment == null) return null;

        InventoryTransactionResponse response = new InventoryTransactionResponse();
        response.setId(adjustment.getId());
        response.setProductId(adjustment.getProduct() != null ? adjustment.getProduct().getId() : null);
        response.setProductName(adjustment.getProduct() != null ? adjustment.getProduct().getName() : null);
        response.setTransactionType(InventoryTransactionType.ADJUSTMENT);
        response.setQuantity(adjustment.getAdjustmentQuantity());
        response.setReferenceId(adjustment.getId());
        
        if (adjustment.getAdjustedBy() != null) {
            response.setCreatedByUserId(adjustment.getAdjustedBy().getId());
            response.setCreatedByUserName(
                adjustment.getAdjustedBy().getFirstName() + " " + 
                adjustment.getAdjustedBy().getLastName()
            );
        }
        
        response.setCreatedAt(adjustment.getCreatedAt());
        response.setNotes("Reason: " + adjustment.getReason());

        return response;
    }

    public InventoryReportResponse.TransactionSummary toTransactionSummary(InventoryTransaction transaction) {
        if (transaction == null) return null;

        InventoryReportResponse.TransactionSummary summary = 
            new InventoryReportResponse.TransactionSummary();
        summary.setType(transaction.getTransactionType());
        summary.setQuantity(transaction.getQuantity());
        summary.setDate(transaction.getCreatedAt());
        
        // Build reference info from the source type
        String referenceInfo = "ID: " + transaction.getReferenceId();
        
        // If we have notes, extract source info
        if (transaction.getNotes() != null && !transaction.getNotes().isEmpty()) {
            referenceInfo = transaction.getNotes();
        }
        
        summary.setReferenceInfo(referenceInfo);

        return summary;
    }

    public List<InventoryReportResponse.TransactionSummary> toTransactionSummaryList(
            List<InventoryTransaction> transactions) {
        return transactions.stream()
            .map(this::toTransactionSummary)
            .collect(Collectors.toList());
    }
}