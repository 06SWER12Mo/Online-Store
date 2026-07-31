package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.repository.ReceiptRepository;

/**
 * Startup normalization: receipts saved in the system are always paid.
 * Flips any legacy PENDING/UNPAID/PARTIAL receipts to APPROVED/PAID.
 */
@Component
public class ReceiptStatusNormalizer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ReceiptStatusNormalizer.class);

    private final ReceiptRepository receiptRepository;

    public ReceiptStatusNormalizer(ReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    @Override
    public void run(String... args) {
        int updated = receiptRepository.normalizeSavedReceipts();
        if (updated > 0) {
            log.info("ReceiptStatusNormalizer: normalized {} legacy receipt(s) to APPROVED/PAID", updated);
        }
    }
}
