package com.example.demo.inventory;

import org.springframework.stereotype.Component;

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
        response.setTransactionType(transaction.getTransactionType());
        response.setQuantity(transaction.getQuantity());
        response.setReferenceType(transaction.getReferenceType());
        response.setReferenceId(transaction.getReferenceId());
        response.setCreatedByUserId(transaction.getCreatedByUser() != null ? transaction.getCreatedByUser().getId() : null);
        response.setCreatedByUserName(transaction.getCreatedByUser() != null 
            ? transaction.getCreatedByUser().getFirstName() + " " + transaction.getCreatedByUser().getLastName() 
            : null);
        response.setCreatedAt(transaction.getCreatedAt());
        response.setNotes(transaction.getNotes());

        return response;
    }

    public List<InventoryTransactionResponse> toInventoryTransactionResponseList(List<InventoryTransaction> transactions) {
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
        response.setTransactionType(InventoryTransactionType.Adjustment);
        response.setQuantity(adjustment.getAdjustmentQuantity());
        response.setReferenceType(InventoryReferenceType.ManualAdjustment);
        response.setReferenceId(adjustment.getId());
        response.setCreatedByUserId(adjustment.getAdjustedBy() != null ? adjustment.getAdjustedBy().getId() : null);
        response.setCreatedByUserName(adjustment.getAdjustedBy() != null 
            ? adjustment.getAdjustedBy().getFirstName() + " " + adjustment.getAdjustedBy().getLastName() 
            : null);
        response.setCreatedAt(adjustment.getCreatedAt());
        response.setNotes(adjustment.getReason());

        return response;
    }

    public InventoryReportResponse.TransactionSummary toTransactionSummary(InventoryTransaction transaction) {
        if (transaction == null) return null;

        InventoryReportResponse.TransactionSummary summary = new InventoryReportResponse.TransactionSummary();
        summary.setType(transaction.getTransactionType());
        summary.setQuantity(transaction.getQuantity());
        summary.setDate(transaction.getCreatedAt());
        
        String refInfo = String.format("%s #%d", transaction.getReferenceType(), transaction.getReferenceId());
        summary.setReferenceInfo(refInfo);

        return summary;
    }

    public List<InventoryReportResponse.TransactionSummary> toTransactionSummaryList(List<InventoryTransaction> transactions) {
        return transactions.stream()
            .map(this::toTransactionSummary)
            .collect(Collectors.toList());
    }
}