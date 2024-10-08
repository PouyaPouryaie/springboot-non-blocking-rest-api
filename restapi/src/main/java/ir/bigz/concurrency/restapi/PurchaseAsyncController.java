package ir.bigz.concurrency.restapi;

import ir.bigz.concurrency.restapi.common.LogSection;
import ir.bigz.concurrency.restapi.common.TestUtils;
import ir.bigz.concurrency.restapi.dto.BatchPurchase;
import ir.bigz.concurrency.restapi.dto.Purchase;
import ir.bigz.concurrency.restapi.service.PurchaseAsyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/purchase/async/v1")
public class PurchaseAsyncController {

    Logger log = LoggerFactory.getLogger(PurchaseController.class);


    private final PurchaseAsyncService purchaseAsyncService;

    public PurchaseAsyncController(PurchaseAsyncService purchaseAsyncService) {
        this.purchaseAsyncService = purchaseAsyncService;
    }

    @GetMapping("/generateData")
    public ResponseEntity<Purchase> purchaseWithGenerateData() {
        Purchase purchase = TestUtils.generatePurchase();
        log.info("Purchase generated {}", purchase);
        long start = System.currentTimeMillis();
        purchaseAsyncService.updatePurchase(purchase);
        TestUtils.elapsedTime(LogSection.ASYNC, log, start, purchase.getOrderId());
        return ResponseEntity.ok(purchase);
    }

    @PostMapping
    public void purchase(@RequestBody Purchase purchase) {
        long start = System.currentTimeMillis();
        purchaseAsyncService.updatePurchase(purchase);
        TestUtils.elapsedTime(LogSection.ASYNC, log, start, purchase.getOrderId());
    }

    @PostMapping("/batch")
    public void batchPurchase(@RequestBody BatchPurchase batchPurchase) {
        long start = System.currentTimeMillis();
        purchaseAsyncService.batchUpdatePurchase(batchPurchase);
        TestUtils.elapsedTime(LogSection.ASYNC, log, start);
    }

    @PostMapping("/future/batch")
    public void batchPurchaseAtFuture(@RequestBody BatchPurchase batchPurchase) {
        long start = System.currentTimeMillis();
        purchaseAsyncService.updatePurchaseAtFuture(batchPurchase);
        TestUtils.elapsedTime(LogSection.ASYNC, log, start);
    }

    @GetMapping("/failed")
    public ResponseEntity<?> failedPurchaseList() {
        Set<Purchase> failedPurchaseList = purchaseAsyncService.getFailedPurchaseList();
        Map<String, Object> result = new HashMap<>();
        result.put("result", failedPurchaseList);
        result.put("size", failedPurchaseList.size());
        return ResponseEntity.ok(result);

    }
}
