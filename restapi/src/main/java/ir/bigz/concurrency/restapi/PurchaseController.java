package ir.bigz.concurrency.restapi;

import ir.bigz.concurrency.restapi.common.LogSection;
import ir.bigz.concurrency.restapi.common.TestUtils;
import ir.bigz.concurrency.restapi.dto.BatchPurchase;
import ir.bigz.concurrency.restapi.dto.Purchase;
import ir.bigz.concurrency.restapi.service.PurchaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/purchase/v1")
public class PurchaseController {

    Logger log = LoggerFactory.getLogger(PurchaseController.class);


    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping("/generateData")
    public ResponseEntity<Purchase> purchaseWithGenerateData() {
        Purchase purchase = TestUtils.generatePurchase();
        log.info("Purchase generated {}", purchase);
        long start = System.currentTimeMillis();
        purchaseService.updatePurchase(purchase);
        TestUtils.elapsedTime(LogSection.NORMAL, log, start, purchase.getOrderId());
        return ResponseEntity.ok(purchase);
    }

    @PostMapping
    public void purchase(@RequestBody Purchase purchase) {
        long start = System.currentTimeMillis();
        purchaseService.updatePurchase(purchase);
        TestUtils.elapsedTime(LogSection.NORMAL, log, start, purchase.getOrderId());
    }

    @PostMapping("/batch")
    public void batchPurchase(@RequestBody BatchPurchase batchPurchase) {
        long start = System.currentTimeMillis();
        purchaseService.batchUpdatePurchase(batchPurchase);
        TestUtils.elapsedTime(LogSection.NORMAL, log, start);
    }

    @GetMapping("/failed")
    public ResponseEntity<?> failedPurchaseList() {
        Set<Purchase> failedPurchaseList = purchaseService.getFailedPurchaseList();
        Map<String, Object> result = new HashMap<>();
        result.put("result", failedPurchaseList);
        result.put("size", failedPurchaseList.size());
        return ResponseEntity.ok(result);

    }

}
