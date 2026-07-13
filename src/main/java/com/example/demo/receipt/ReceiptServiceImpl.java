package com.example.demo.receipt;

import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.inventory.InventoryService;
import com.example.demo.inventory.InventoryTransactionType;
import com.example.demo.product.Product;
import com.example.demo.product.ProductRepository;
import com.example.demo.receipt.dtos.ReceiptItemRequest;
import com.example.demo.receipt.dtos.ReceiptRequest;
import com.example.demo.receipt.dtos.ReceiptResponse;
import com.example.demo.receipt.dtos.SupplierRequest;
import com.example.demo.receipt.dtos.SupplierResponse;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReceiptServiceImpl implements ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final ReceiptItemRepository receiptItemRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReceiptMapper receiptMapper;
    
    // ✅ CLEAN INVENTORY INTEGRATION (NO REFERENCE TYPE!)
    private final InventoryService inventoryService;

    public ReceiptServiceImpl(ReceiptRepository receiptRepository,
                              ReceiptItemRepository receiptItemRepository,
                              SupplierRepository supplierRepository,
                              ProductRepository productRepository,
                              UserRepository userRepository,
                              ReceiptMapper receiptMapper,
                              InventoryService inventoryService) {
        this.receiptRepository = receiptRepository;
        this.receiptItemRepository = receiptItemRepository;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.receiptMapper = receiptMapper;
        this.inventoryService = inventoryService;
    }

    // ========== RECEIPT OPERATIONS ==========

    @Override
    public ReceiptResponse createReceipt(ReceiptRequest request, Long userId) {
        // Validate supplier exists
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + request.getSupplierId()));

        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.userById(userId));

        // Create receipt
        Receipt receipt = new Receipt();
        receipt.setReceiptNumber(generateReceiptNumber());
        receipt.setReceiptDate(request.getReceiptDate() != null ? request.getReceiptDate() : LocalDateTime.now());
        receipt.setStatus("PENDING");
        receipt.setPaymentStatus("UNPAID");
        receipt.setReceiptType(request.getReceiptType() != null ? request.getReceiptType() : "PURCHASE");
        receipt.setShippingCost(request.getShippingCost() != null ? request.getShippingCost() : BigDecimal.ZERO);
        receipt.setDiscountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO);
        receipt.setTaxAmount(BigDecimal.ZERO);
        receipt.setSubtotal(BigDecimal.ZERO);
        receipt.setTotalAmount(BigDecimal.ZERO);
        receipt.setSupplier(supplier);
        receipt.setCreatedBy(user);
        receipt.setNotes(request.getNotes());
        receipt.setPaymentMethod(request.getPaymentMethod());

        // Save receipt first
        Receipt savedReceipt = receiptRepository.save(receipt);

        // Process items
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (ReceiptItemRequest itemRequest : request.getItems()) {
                Product product = productRepository.findById(itemRequest.getProductId())
                        .orElseThrow(() -> ResourceNotFoundException.productById(itemRequest.getProductId()));

                // Create receipt item
                ReceiptItem item = receiptMapper.toItemEntity(itemRequest, savedReceipt);
                savedReceipt.addItem(item);

                // ✅ UPDATE PRODUCT STOCK
                int newStock = product.getStockQuantity() + itemRequest.getQuantity();
                product.setStockQuantity(newStock);
                product.setInStock(newStock > 0);
                productRepository.save(product);

                // ✅ CREATE INVENTORY TRANSACTION - NO REFERENCE TYPE NEEDED!
                inventoryService.createInventoryTransaction(
                    product.getId(),                                    // Product ID
                    InventoryTransactionType.RECEIVED_STOCK,            // Transaction Type
                    itemRequest.getQuantity(),                          // Quantity
                    savedReceipt.getId(),                               // Reference ID (Receipt ID)
                    userId,                                             // User ID
                    "Received from supplier: " + supplier.getName() +   // Notes
                    " | Receipt: " + savedReceipt.getReceiptNumber() +
                    " | Product: " + product.getName()
                );
            }
        }

        // Recalculate totals
        savedReceipt.recalculateTotals();

        Receipt saved = receiptRepository.save(savedReceipt);
        return receiptMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ReceiptResponse getReceiptById(Long id) {
        Receipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt not found with id: " + id));
        return receiptMapper.toResponse(receipt);
    }

    @Override
    @Transactional(readOnly = true)
    public ReceiptResponse getReceiptByNumber(String receiptNumber) {
        Receipt receipt = receiptRepository.findByReceiptNumber(receiptNumber)
                .orElseThrow(() -> new RuntimeException("Receipt not found with number: " + receiptNumber));
        return receiptMapper.toResponse(receipt);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReceiptResponse> getAllReceipts(Pageable pageable) {
        return receiptRepository.findAll(pageable)
                .map(receiptMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReceiptResponse> getReceiptsBySupplier(Long supplierId, Pageable pageable) {
        return receiptRepository.findBySupplierId(supplierId, pageable)
                .map(receiptMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReceiptResponse> getReceiptsByStatus(String status, Pageable pageable) {
        return receiptRepository.findByStatus(status, pageable)
                .map(receiptMapper::toResponse);
    }

    @Override
    public ReceiptResponse updateReceiptStatus(Long id, String status) {
        Receipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt not found with id: " + id));
        
        receipt.setStatus(status);
        if ("APPROVED".equals(status)) {
            receipt.setApprovedAt(LocalDateTime.now());
        }
        
        Receipt updated = receiptRepository.save(receipt);
        return receiptMapper.toResponse(updated);
    }

    @Override
    public ReceiptResponse updatePaymentStatus(Long id, String paymentStatus) {
        Receipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt not found with id: " + id));
        
        receipt.setPaymentStatus(paymentStatus);
        Receipt updated = receiptRepository.save(receipt);
        return receiptMapper.toResponse(updated);
    }

    @Override
    public ReceiptResponse approveReceipt(Long id, Long userId) {
        Receipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt not found with id: " + id));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.userById(userId));

        receipt.setStatus("APPROVED");
        receipt.setApprovedBy(user);
        receipt.setApprovedAt(LocalDateTime.now());
        
        Receipt updated = receiptRepository.save(receipt);
        return receiptMapper.toResponse(updated);
    }

    @Override
    public void deleteReceipt(Long id) {
        Receipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt not found with id: " + id));
        
        // Reverse stock changes if receipt is approved
        if ("APPROVED".equals(receipt.getStatus())) {
            for (ReceiptItem item : receipt.getItems()) {
                Product product = item.getProduct();
                int newStock = product.getStockQuantity() - item.getQuantity();
                product.setStockQuantity(Math.max(0, newStock));
                product.setInStock(newStock > 0);
                productRepository.save(product);
                
                // ✅ REVERSE INVENTORY TRANSACTION - NO REFERENCE TYPE NEEDED!
                inventoryService.createInventoryTransaction(
                    product.getId(),                                    // Product ID
                    InventoryTransactionType.ADJUSTMENT,                // Transaction Type
                    item.getQuantity(),                                 // Quantity
                    receipt.getId(),                                    // Reference ID
                    null,                                               // User ID (system action)
                    "Stock reversed: Receipt " + receipt.getReceiptNumber() + 
                    " deleted | Product: " + product.getName() +
                    " | Quantity: " + item.getQuantity()
                );
            }
        }
        
        receiptRepository.delete(receipt);
    }

    // ========== RECEIPT STATISTICS ==========

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalReceiptsAmountBetween(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal total = receiptRepository.getTotalReceiptsAmountBetween(startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public long countApprovedReceiptsBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return receiptRepository.countApprovedReceiptsBetween(startDate, endDate);
    }

    // ========== SUPPLIER OPERATIONS ==========

    @Override
    public SupplierResponse createSupplier(SupplierRequest request) {
        // Check if supplier code already exists
        if (supplierRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Supplier with code '" + request.getCode() + "' already exists");
        }

        // Check if supplier email already exists
        if (request.getEmail() != null && supplierRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Supplier with email '" + request.getEmail() + "' already exists");
        }

        Supplier supplier = new Supplier();
        supplier.setName(request.getName());
        supplier.setCode(request.getCode());
        supplier.setAddress(request.getAddress());
        supplier.setCity(request.getCity());
        supplier.setState(request.getState());
        supplier.setZipCode(request.getZipCode());
        supplier.setCountry(request.getCountry());
        supplier.setPhone(request.getPhone());
        supplier.setEmail(request.getEmail());
        supplier.setWebsite(request.getWebsite());
        supplier.setContactPerson(request.getContactPerson());
        supplier.setContactPhone(request.getContactPhone());
        supplier.setContactEmail(request.getContactEmail());
        supplier.setTaxId(request.getTaxId());
        supplier.setRegistrationNumber(request.getRegistrationNumber());
        supplier.setNotes(request.getNotes());
        supplier.setPaymentTerms(request.getPaymentTerms());
        supplier.setDeliveryTerms(request.getDeliveryTerms());
        supplier.setActive(true);

        Supplier saved = supplierRepository.save(supplier);
        return receiptMapper.toSupplierResponse(saved);
    }

    @Override
    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));

        // Check if code is being changed and already exists
        if (request.getCode() != null && !request.getCode().equals(supplier.getCode())) {
            if (supplierRepository.existsByCodeAndIdNot(request.getCode(), id)) {
                throw new RuntimeException("Supplier with code '" + request.getCode() + "' already exists");
            }
        }

        // Check if email is being changed and already exists
        if (request.getEmail() != null && !request.getEmail().equals(supplier.getEmail())) {
            if (supplierRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
                throw new RuntimeException("Supplier with email '" + request.getEmail() + "' already exists");
            }
        }

        if (request.getName() != null) supplier.setName(request.getName());
        if (request.getCode() != null) supplier.setCode(request.getCode());
        if (request.getAddress() != null) supplier.setAddress(request.getAddress());
        if (request.getCity() != null) supplier.setCity(request.getCity());
        if (request.getState() != null) supplier.setState(request.getState());
        if (request.getZipCode() != null) supplier.setZipCode(request.getZipCode());
        if (request.getCountry() != null) supplier.setCountry(request.getCountry());
        if (request.getPhone() != null) supplier.setPhone(request.getPhone());
        if (request.getEmail() != null) supplier.setEmail(request.getEmail());
        if (request.getWebsite() != null) supplier.setWebsite(request.getWebsite());
        if (request.getContactPerson() != null) supplier.setContactPerson(request.getContactPerson());
        if (request.getContactPhone() != null) supplier.setContactPhone(request.getContactPhone());
        if (request.getContactEmail() != null) supplier.setContactEmail(request.getContactEmail());
        if (request.getTaxId() != null) supplier.setTaxId(request.getTaxId());
        if (request.getRegistrationNumber() != null) supplier.setRegistrationNumber(request.getRegistrationNumber());
        if (request.getNotes() != null) supplier.setNotes(request.getNotes());
        if (request.getPaymentTerms() != null) supplier.setPaymentTerms(request.getPaymentTerms());
        if (request.getDeliveryTerms() != null) supplier.setDeliveryTerms(request.getDeliveryTerms());

        Supplier updated = supplierRepository.save(supplier);
        return receiptMapper.toSupplierResponse(updated);
    }

    @Override
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));

        // Check if supplier has receipts
        long receiptCount = supplierRepository.countReceiptsBySupplierId(id);
        if (receiptCount > 0) {
            throw new RuntimeException("Cannot delete supplier with existing receipts. Deactivate instead.");
        }

        supplierRepository.delete(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponse getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
        return receiptMapper.toSupplierResponse(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierResponse> getAllSuppliers(Pageable pageable) {
        return supplierRepository.findAll(pageable)
                .map(receiptMapper::toSupplierResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponse> searchSuppliers(String keyword) {
        return supplierRepository.searchSuppliers(keyword)
                .stream()
                .map(receiptMapper::toSupplierResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void toggleSupplierActive(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
        supplier.setActive(!supplier.isActive());
        supplierRepository.save(supplier);
    }

    // ========== HELPER METHODS ==========

    private String generateReceiptNumber() {
        String prefix = "RCP";
        String timestamp = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return prefix + "-" + timestamp + "-" + uuid;
    }
}