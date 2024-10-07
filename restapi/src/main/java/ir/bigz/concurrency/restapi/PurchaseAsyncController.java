package ir.bigz.concurrency.restapi;

import ir.bigz.concurrency.restapi.dto.BatchPurchase;
import ir.bigz.concurrency.restapi.dto.Purchase;
import ir.bigz.concurrency.restapi.service.PurchaseAsyncService;
import ir.bigz.concurrency.restapi.service.PurchaseService;
import org.instancio.Instancio;
import org.instancio.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public void purchaseWithGenerateData() {
        Purchase purchase = TestUtils.generatePurchase();
        log.info("Purchase generated {}", purchase);
        long start = System.currentTimeMillis();
        purchaseAsyncService.updatePurchase(purchase);
        elapsedTime(start, purchase.getOrderId());
    }

    @PostMapping
    public void purchase(@RequestBody Purchase purchase) {
        long start = System.currentTimeMillis();
        purchaseAsyncService.updatePurchase(purchase);
        elapsedTime(start, purchase.getOrderId());
    }

    @PostMapping("/batch")
    public void batchPurchase(@RequestBody BatchPurchase batchPurchase) {
        long start = System.currentTimeMillis();
        purchaseAsyncService.batchUpdatePurchase(batchPurchase);
        elapsedTime(start);
    }

    @PostMapping("/future/batch")
    public void batchPurchaseAtFuture(@RequestBody BatchPurchase batchPurchase) {
        long start = System.currentTimeMillis();
        purchaseAsyncService.updatePurchaseAtFuture(batchPurchase);
        elapsedTime(start);
    }

    @GetMapping("/failed")
    public ResponseEntity<?> failedPurchaseList() {
        Set<Purchase> failedPurchaseList = purchaseAsyncService.getFailedPurchaseList();
        return ResponseEntity.ok(failedPurchaseList);

    }

    private void elapsedTime(long start, long orderId) {
        long finish = System.currentTimeMillis();
        log.info("### Async: orderId: [{}] The time is passed in {} ms",orderId, finish - start);
    }

    private void elapsedTime(long start) {
        long finish = System.currentTimeMillis();
        log.info("### Async: The time is passed in {} ms",finish - start);
    }
}
