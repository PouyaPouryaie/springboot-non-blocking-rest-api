package ir.bigz.concurrency.restapi;

import ir.bigz.concurrency.restapi.dto.Purchase;
import ir.bigz.concurrency.restapi.service.PurchaseReactiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/purchase/reactive/v1")
public class PurchaseReactiveController {

    Logger log = LoggerFactory.getLogger(PurchaseReactiveController.class);
    private final PurchaseReactiveService purchaseService;

    public PurchaseReactiveController(PurchaseReactiveService purchaseReactiveService) {
        this.purchaseService = purchaseReactiveService;
    }

    @GetMapping("/generateData")
    public void purchaseWithGenerateData() {
        Purchase purchase = TestUtils.generatePurchase();
        log.info("Purchase generated {}", purchase);
        long start = System.currentTimeMillis();
        purchaseService.updatePurchase(purchase);
        TestUtils.elapsedTime(log, start, purchase.getOrderId());
    }
}
