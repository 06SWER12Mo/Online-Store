package com.example.demo.shipping;

import com.example.demo.location.BigArea;
import com.example.demo.location.BigAreaRepository;
import com.example.demo.shipping.dtos.ShippingBatchResponse;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // ========== DAILY BATCH CREATION (9 AM) ==========

    @Override
    @Scheduled(cron = "0 0 9 * * *")
    public void autoCreateBatchesForBigAreas() {
        System.out.println("🔄 Creating daily batches for all BigAreas...");
        
        List<BigArea> bigAreas = bigAreaRepository.findAll();
        int createdCount = 0;

        for (BigArea bigArea : bigAreas) {
            boolean hasCollectingBatch = shippingBatchRepository
                .findCollectingBatchByBigAreaId(bigArea.getId())
                .isPresent();

            if (!hasCollectingBatch) {
                shippingService.createBatch(bigArea.getId(), 10);
                createdCount++;
                System.out.println("✅ Created new batch for BigArea: " + bigArea.getName());
            }
        }
        
        if (createdCount > 0) {
            System.out.println("✅ Created " + createdCount + " new batches!");
        }
    }

    // ========== CHECK BATCH READINESS (Every 4 Hours) ==========

    @Override
    @Scheduled(cron = "0 0 */4 * * *")
    public void checkAndMarkBatchesReadyToDispatch() {
        System.out.println("🔍 Checking for batches ready to dispatch...");
        
        List<ShippingBatch> collectingBatches = shippingBatchRepository
            .findByStatus(ShippingStatus.COLLECTING_ORDERS);

        int readyCount = 0;
        int busAssignedCount = 0;

        for (ShippingBatch batch : collectingBatches) {
            if (batch.isReadyToDispatch()) {
               
                shippingService.markBatchReadyToDispatch(batch.getId());
                readyCount++;
                System.out.println("✅ Batch #" + batch.getId() + " is now READY_TO_DISPATCH");
                
               
                try {
                    shippingService.autoAssignBus(batch.getId());
                    busAssignedCount++;
                    System.out.println("🚌 Auto-assigned bus to Batch #" + batch.getId());
                } catch (Exception e) {
                    System.err.println("❌ Failed to auto-assign bus to Batch #" + batch.getId() + ": " + e.getMessage());
                }
            }
        }
        
        if (readyCount > 0) {
            System.out.println("✅ Marked " + readyCount + " batches as READY_TO_DISPATCH");
        }
        if (busAssignedCount > 0) {
            System.out.println("🚌 Auto-assigned buses to " + busAssignedCount + " batches");
        }
    }

    // ========== AUTO-DISPATCH (6 PM Daily) ==========

    @Override
    @Scheduled(cron = "0 0 18 * * *")
    public void autoDispatchReadyBatches() {
        System.out.println("🚀 Auto-dispatching ready batches...");
        
        List<ShippingBatch> readyBatches = shippingBatchRepository.findReadyToDispatchBatches();
        int dispatchedCount = 0;

        for (ShippingBatch batch : readyBatches) {
            //  Only dispatch if bus is assigned
            if (batch.getBus() != null) {
                shippingService.dispatchBatch(batch.getId());
                dispatchedCount++;
                System.out.println("🚚 Dispatched Batch #" + batch.getId() + 
                                   " with Bus " + batch.getBus().getPlateNumber());
            } else {
                //  Try to auto-assign a bus before dispatch
                try {
                    shippingService.autoAssignBus(batch.getId());
                    shippingService.dispatchBatch(batch.getId());
                    dispatchedCount++;
                    System.out.println("🚚 Dispatched Batch #" + batch.getId() + 
                                       " with newly assigned Bus " + batch.getBus().getPlateNumber());
                } catch (Exception e) {
                    System.err.println("❌ Could not dispatch Batch #" + batch.getId() + 
                                       ": " + e.getMessage());
                }
            }
        }
        
        if (dispatchedCount > 0) {
            System.out.println("✅ Dispatched " + dispatchedCount + " batches!");
        }
    }

    // ========== AUTO-DELIVERY (Every 5 Minutes) ==========

    @Override
    @Scheduled(cron = "0 */5 * * * *")
    public void autoDeliverBatches() {
        System.out.println("⏰ Checking for batches to auto-deliver...");
        
        List<ShippingBatch> batchesToDeliver = shippingService.getBatchesToAutoDeliver();
        
        for (ShippingBatch batch : batchesToDeliver) {
            shippingService.autoDeliverBatch(batch.getId());
            System.out.println("✅ Batch #" + batch.getId() + " auto-delivered!");
        }
        
        if (!batchesToDeliver.isEmpty()) {
            System.out.println("🚚 Auto-delivered " + batchesToDeliver.size() + " batches!");
        }
    }

    // ========== GET PENDING DISPATCHES ==========

    @Override
    @Transactional(readOnly = true)
    public List<ShippingBatchResponse> getPendingDispatches() {
        List<ShippingBatch> dispatchedBatches = shippingBatchRepository
            .findByStatus(ShippingStatus.DISPATCHED);
        return new ShippingMapper().toShippingBatchResponseList(dispatchedBatches);
    }

    @Override
    public void processBatchDispatch(Long batchId) {
        shippingService.dispatchBatch(batchId);
    }
}