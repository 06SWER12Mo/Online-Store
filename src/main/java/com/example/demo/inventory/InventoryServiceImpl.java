package com.example.demo.inventory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.product.Product;
import com.example.demo.product.ProductRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;

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

    // ========== TRANSACTION OPERATIONS ==========

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
    public List<InventoryTransactionResponse> getTransactionsByReference(InventoryReferenceType referenceType, Long referenceId) {
        return inventoryMapper.toInventoryTransactionResponseList(
            inventoryTransactionRepository.findByReferenceTypeAndReferenceId(referenceType, referenceId));
    }

    // ========== STOCK ADJUSTMENT OPERATIONS ==========

    @Override
    public InventoryTransactionResponse adjustStock(StockAdjustmentRequest request, Long adjustedByUserId) {
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> ResourceNotFoundException.productById(request.getProductId()));

        User adjustedBy = userRepository.findById(adjustedByUserId)
            .orElseThrow(() -> ResourceNotFoundException.userById(adjustedByUserId));

        Integer previousQuantity = product.getStockQuantity();
        Integer newQuantity = request.getNewQuantity();
        Integer adjustmentQuantity = newQuantity - previousQuantity;

        // Update product stock
        product.setStockQuantity(newQuantity);
        productRepository.save(product);

        // Create stock adjustment record
        StockAdjustment adjustment = new StockAdjustment();
        adjustment.setProduct(product);
        adjustment.setPreviousQuantity(previousQuantity);
        adjustment.setNewQuantity(newQuantity);
        adjustment.setAdjustmentQuantity(adjustmentQuantity);
        adjustment.setReason(request.getReason());
        adjustment.setAdjustedBy(adjustedBy);
        stockAdjustmentRepository.save(adjustment);

        // Create inventory transaction
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setProduct(product);
        transaction.setTransactionType(InventoryTransactionType.Adjustment);
        transaction.setQuantity(Math.abs(adjustmentQuantity));
        transaction.setReferenceType(InventoryReferenceType.ManualAdjustment);
        transaction.setReferenceId(adjustment.getId());
        transaction.setCreatedByUser(adjustedBy);
        transaction.setNotes("Stock adjusted from " + previousQuantity + " to " + newQuantity + ". Reason: " + request.getReason());
        inventoryTransactionRepository.save(transaction);

        return inventoryMapper.toInventoryTransactionResponse(transaction);
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
        report.setProductSku(product.getSku());  // FIXED: using setProductSku
        report.setCurrentStock(product.getStockQuantity());
        report.setCurrentStockValue(
            product.getPrice().multiply(BigDecimal.valueOf(product.getStockQuantity())));

        // Calculate totals by transaction type - FIXED: using Integer directly
        report.setTotalReceived(
            inventoryTransactionRepository.sumQuantityByProductAndType(productId, InventoryTransactionType.ReceivedStock));
        report.setTotalSold(
            inventoryTransactionRepository.sumQuantityByProductAndType(productId, InventoryTransactionType.Sale));
        report.setTotalReturned(
            inventoryTransactionRepository.sumQuantityByProductAndType(productId, InventoryTransactionType.Return));
        report.setTotalDamaged(
            inventoryTransactionRepository.sumQuantityByProductAndType(productId, InventoryTransactionType.Damaged));
        report.setTotalAdjusted(
            inventoryTransactionRepository.sumQuantityByProductAndType(productId, InventoryTransactionType.Adjustment));

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

    // ========== STOCK LEVEL OPERATIONS ==========

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
}