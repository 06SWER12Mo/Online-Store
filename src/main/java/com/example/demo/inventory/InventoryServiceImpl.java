package com.example.demo.inventory;

import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.inventory.dtos.InventoryReportResponse;
import com.example.demo.inventory.dtos.InventoryTransactionResponse;
import com.example.demo.inventory.dtos.StockAdjustmentRequest;
import com.example.demo.product.Product;
import com.example.demo.product.ProductRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final InventoryMapper inventoryMapper;

    public InventoryServiceImpl(
            InventoryTransactionRepository inventoryTransactionRepository,
            StockAdjustmentRepository stockAdjustmentRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            InventoryMapper inventoryMapper) {
        this.inventoryTransactionRepository = inventoryTransactionRepository;
        this.stockAdjustmentRepository = stockAdjustmentRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.inventoryMapper = inventoryMapper;
    }

    // ========== ✅ CORE TRANSACTION METHOD - NOW UPDATES PRODUCT STOCK! ==========

    @Override
    public void createInventoryTransaction(
            Long productId,
            InventoryTransactionType type,
            Integer quantity,
            Long referenceId,
            Long userId,
            String notes) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> ResourceNotFoundException.productById(productId));

        // ✅ DETERMINE STOCK CHANGE BASED ON TRANSACTION TYPE
        int stockChange = 0;
        switch (type) {
            case RECEIVED_STOCK:
            case RETURN:
                stockChange = quantity;  // Stock IN (positive)
                break;
            case SALE:
            case DAMAGED:
                stockChange = -quantity; // Stock OUT (negative)
                break;
            case ADJUSTMENT:
                // For adjustment, quantity is already calculated as the change
                stockChange = quantity;
                break;
        }

        // ✅ UPDATE PRODUCT STOCK
        int newStock = product.getStockQuantity() + stockChange;
        product.setStockQuantity(Math.max(0, newStock)); // Don't go below zero
        product.setInStock(product.getStockQuantity() > 0);
        productRepository.save(product);

        // ✅ CREATE INVENTORY TRANSACTION
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> ResourceNotFoundException.userById(userId));
        }

        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setProduct(product);
        transaction.setTransactionType(type);
        transaction.setQuantity(Math.abs(quantity)); // Always store positive
        transaction.setReferenceId(referenceId);
        transaction.setNotes(notes);
        transaction.setCreatedByUser(user);

        inventoryTransactionRepository.save(transaction);
    }

    // ========== TRANSACTION QUERIES ==========

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransactionResponse> getAllTransactions() {
        return inventoryMapper.toInventoryTransactionResponseList(
            inventoryTransactionRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventoryTransactionResponse> getAllTransactions(Pageable pageable) {
        return inventoryTransactionRepository.findAll(pageable)
            .map(inventoryMapper::toInventoryTransactionResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryTransactionResponse getTransactionById(Long id) {
        InventoryTransaction transaction = inventoryTransactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Inventory transaction not found with id: " + id));
        return inventoryMapper.toInventoryTransactionResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransactionResponse> getTransactionsByProductId(Long productId) {
        return inventoryMapper.toInventoryTransactionResponseList(
            inventoryTransactionRepository.findByProductId(productId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventoryTransactionResponse> getTransactionsByProductId(Long productId, Pageable pageable) {
        return inventoryTransactionRepository.findByProductId(productId, pageable)
            .map(inventoryMapper::toInventoryTransactionResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransactionResponse> getTransactionsByType(InventoryTransactionType type) {
        return inventoryMapper.toInventoryTransactionResponseList(
            inventoryTransactionRepository.findByTransactionType(type));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransactionResponse> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return inventoryMapper.toInventoryTransactionResponseList(
            inventoryTransactionRepository.findByDateRange(startDate, endDate));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransactionResponse> getTransactionsByReferenceId(Long referenceId) {
        return inventoryMapper.toInventoryTransactionResponseList(
            inventoryTransactionRepository.findByReferenceId(referenceId));
    }

    // ========== STOCK ADJUSTMENT - FIXED ==========

    @Override
    public InventoryTransactionResponse adjustStock(StockAdjustmentRequest request, Long adjustedByUserId) {
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> ResourceNotFoundException.productById(request.getProductId()));

        User adjustedBy = userRepository.findById(adjustedByUserId)
            .orElseThrow(() -> ResourceNotFoundException.userById(adjustedByUserId));

        // ✅ adjustmentDelta is the CHANGE to apply (positive = add, negative = remove)
        Integer adjustmentDelta = request.getAdjustmentDelta();
        Integer previousQuantity = product.getStockQuantity();
        Integer newQuantity = previousQuantity + adjustmentDelta;
        
        // Don't allow negative stock
        if (newQuantity < 0) {
            throw new RuntimeException(
                "Cannot adjust stock. Current quantity: " + previousQuantity + 
                ", Adjustment: " + adjustmentDelta + 
                " would result in negative stock: " + newQuantity
            );
        }

        // Update product stock
        product.setStockQuantity(newQuantity);
        product.setInStock(newQuantity > 0);
        productRepository.save(product);

        // Create stock adjustment record
        StockAdjustment adjustment = new StockAdjustment();
        adjustment.setProduct(product);
        adjustment.setPreviousQuantity(previousQuantity);
        adjustment.setNewQuantity(newQuantity);
        adjustment.setAdjustmentQuantity(adjustmentDelta);
        adjustment.setReason(request.getReason());
        adjustment.setAdjustedBy(adjustedBy);
        stockAdjustmentRepository.save(adjustment);

        // ✅ CREATE INVENTORY TRANSACTION - passes the adjustment delta
        createInventoryTransaction(
            product.getId(),
            InventoryTransactionType.ADJUSTMENT,
            adjustmentDelta, // Pass the actual change (positive or negative)
            adjustment.getId(),
            adjustedByUserId,
            "Stock adjusted from " + previousQuantity + " to " + newQuantity + 
            " (delta: " + adjustmentDelta + "). Reason: " + request.getReason()
        );

        // Get the created transaction
        List<InventoryTransaction> transactions = inventoryTransactionRepository
            .findByReferenceId(adjustment.getId());
        
        if (!transactions.isEmpty()) {
            return inventoryMapper.toInventoryTransactionResponse(transactions.get(0));
        }
        
        throw new RuntimeException("Failed to create inventory transaction");
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransactionResponse> getStockAdjustmentsByProductId(Long productId) {
        List<StockAdjustment> adjustments = stockAdjustmentRepository.findByProductId(productId);
        return adjustments.stream()
            .map(inventoryMapper::toTransactionResponseFromAdjustment)
            .collect(Collectors.toList());
    }

    // ========== INVENTORY REPORTS ==========

    @Override
    @Transactional(readOnly = true)
    public InventoryReportResponse getInventoryReportByProductId(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> ResourceNotFoundException.productById(productId));

        InventoryReportResponse report = new InventoryReportResponse();
        report.setProductId(product.getId());
        report.setProductName(product.getName());
        report.setProductSku(product.getSku());
        report.setCurrentStock(product.getStockQuantity());
        report.setCurrentStockValue(
            product.getPrice().multiply(BigDecimal.valueOf(product.getStockQuantity())));

        // Calculate totals by transaction type
        report.setTotalReceived(
            inventoryTransactionRepository.sumQuantityByProductAndType(productId, InventoryTransactionType.RECEIVED_STOCK));
        report.setTotalSold(
            inventoryTransactionRepository.sumQuantityByProductAndType(productId, InventoryTransactionType.SALE));
        report.setTotalReturned(
            inventoryTransactionRepository.sumQuantityByProductAndType(productId, InventoryTransactionType.RETURN));
        report.setTotalDamaged(
            inventoryTransactionRepository.sumQuantityByProductAndType(productId, InventoryTransactionType.DAMAGED));
        report.setTotalAdjusted(
            inventoryTransactionRepository.sumQuantityByProductAndType(productId, InventoryTransactionType.ADJUSTMENT));

        // Get recent transactions
        List<InventoryTransaction> recentTransactions = inventoryTransactionRepository.findByProductId(productId);
        if (!recentTransactions.isEmpty()) {
            report.setLastTransactionDate(recentTransactions.get(0).getCreatedAt());
            int limit = Math.min(5, recentTransactions.size());
            report.setRecentTransactions(
                inventoryMapper.toTransactionSummaryList(recentTransactions.subList(0, limit)));
        }

        return report;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryReportResponse> getAllInventoryReports() {
        List<Product> products = productRepository.findAll();
        return products.stream()
            .map(p -> getInventoryReportByProductId(p.getId()))
            .collect(Collectors.toList());
    }

    // ========== STOCK LEVELS ==========

    @Override
    @Transactional(readOnly = true)
    public Integer getCurrentStockByProductId(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> ResourceNotFoundException.productById(productId));
        return product.getStockQuantity();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryReportResponse> getLowStockProducts(Integer threshold) {
        List<Product> products = productRepository.findAll().stream()
            .filter(p -> p.getStockQuantity() > 0 && p.getStockQuantity() <= threshold)
            .collect(Collectors.toList());

        return products.stream()
            .map(p -> getInventoryReportByProductId(p.getId()))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryReportResponse> getOutOfStockProducts() {
        List<Product> products = productRepository.findAll().stream()
            .filter(p -> p.getStockQuantity() == 0)
            .collect(Collectors.toList());

        return products.stream()
            .map(p -> getInventoryReportByProductId(p.getId()))
            .collect(Collectors.toList());
    }

    // ========== DASHBOARD STATISTICS ==========

    @Override
    @Transactional(readOnly = true)
    public Long getTotalTransactionCount() {
        return inventoryTransactionRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTransactionCountByType(InventoryTransactionType type) {
        return inventoryTransactionRepository.countByTransactionType(type);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getTotalStockValue() {
        return productRepository.findAll().stream()
            .mapToInt(Product::getStockQuantity)
            .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getTotalReceivedStock() {
        return inventoryTransactionRepository.getTotalReceivedStock();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getTotalSoldStock() {
        return inventoryTransactionRepository.getTotalSoldStock();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getTotalDamagedStock() {
        return inventoryTransactionRepository.getTotalDamagedStock();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getTotalReturnedStock() {
        return inventoryTransactionRepository.getTotalReturnedStock();
    }
}