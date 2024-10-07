package ir.bigz.concurrency.restapi;

import ir.bigz.concurrency.restapi.dto.BatchPurchase;
import ir.bigz.concurrency.restapi.dto.Purchase;
import ir.bigz.concurrency.restapi.service.PurchaseService;
import org.instancio.Instancio;
import org.instancio.Select;
import org.instancio.TargetSelector;
import org.instancio.internal.selectors.TargetField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/purchase/v1")
public class PurchaseController {

    Logger log = LoggerFactory.getLogger(PurchaseController.class);


    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping("/generateData")
    public void purchaseWithGenerateData() {
        Purchase purchase = TestUtils.generatePurchase();
        log.info("Purchase generated {}", purchase);
        long start = System.currentTimeMillis();
        purchaseService.updatePurchase(purchase);
        elapsedTime(start);
    }

    @PostMapping
    public void purchase(@RequestBody Purchase purchase) {
        long start = System.currentTimeMillis();
        purchaseService.updatePurchase(purchase);
        elapsedTime(start);
    }

    @PostMapping("/batch")
    public void batchPurchase(@RequestBody BatchPurchase batchPurchase) {
        long start = System.currentTimeMillis();
        purchaseService.batchUpdatePurchase(batchPurchase);
        elapsedTime(start);
    }

    private void elapsedTime(long start) {
        long finish = System.currentTimeMillis();
        log.info("### Normal: The time is passed in {} ms",finish - start);
    }

}
