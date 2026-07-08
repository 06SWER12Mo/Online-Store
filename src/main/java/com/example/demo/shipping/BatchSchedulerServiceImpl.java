package com.example.demo.shipping;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.location.BigArea;
import com.example.demo.location.BigAreaRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class BatchSchedulerServiceImpl implements BatchSchedulerService {

    private final ShippingBatchRepository shippingBatchRepository;
    private final ShippingService shippingService;
    private final BigAreaRepository bigAreaRepository;

    public BatchSchedulerServiceImpl(
            ShippingBatchRepository shippingBatchRepository,
            ShippingService shippingService,
            BigAreaRepository bigAreaRepository) {
        this.shippingBatchRepository = shippingBatchRepository;
        this.shippingService = shippingService;
        this.bigAreaRepository = bigAreaRepository;
    }

    @Override
    @Scheduled(cron = "0 0 9 * * *") // Daily at 9 AM
    public void autoCreateBatchesForBigAreas() {
        List<BigArea> bigAreas = bigAreaRepository.findAll();
        for (BigArea bigArea : bigAreas) {
            // Check if there's already a collecting batch for this area
            boolean hasCollectingBatch = shippingBatchRepository
                .findCollectingBatchByBigAreaId(bigArea.getId())
                .isPresent();

            if (!hasCollectingBatch) {
                shippingService.createBatch(bigArea.getId(), 10);
            }
        }
    }

    @Override
    @Scheduled(cron = "0 0 18 * * *") // Daily at 6 PM
    public void autoDispatchReadyBatches() {
        List<ShippingBatch> readyBatches = shippingBatchRepository.findReadyToDispatchBatches();
        for (ShippingBatch batch : readyBatches) {
            if (batch.getBus() != null) {
                shippingService.dispatchBatch(batch.getId());
            }
        }
    }

    @Override
    @Scheduled(cron = "0 0 */4 * * *") // Every 4 hours
    public void checkAndMarkBatchesReadyToDispatch() {
        List<ShippingBatch> collectingBatches = shippingBatchRepository
            .findByStatus(ShippingStatus.CollectingOrders);

        for (ShippingBatch batch : collectingBatches) {
            if (batch.isReadyToDispatch()) {
                shippingService.markBatchReadyToDispatch(batch.getId());
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShippingBatchResponse> getPendingDispatches() {
        List<ShippingBatch> dispatchedBatches = shippingBatchRepository
            .findByStatus(ShippingStatus.Dispatched);
        return new ShippingMapper().toShippingBatchResponseList(dispatchedBatches);
    }

    @Override
    public void processBatchDispatch(Long batchId) {
        shippingService.dispatchBatch(batchId);
    }
}