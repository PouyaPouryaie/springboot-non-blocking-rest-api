package ir.bigz.concurrency.restapi;

import ir.bigz.concurrency.restapi.common.LogSection;
import ir.bigz.concurrency.restapi.common.TestUtils;
import ir.bigz.concurrency.restapi.dto.Purchase;
import ir.bigz.concurrency.restapi.service.PurchaseReactiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/purchase/reactive/v1")
public class PurchaseReactiveController {

    Logger log = LoggerFactory.getLogger(PurchaseReactiveController.class);
    private final PurchaseReactiveService purchaseService;

    public PurchaseReactiveController(PurchaseReactiveService purchaseReactiveService) {
        this.purchaseService = purchaseReactiveService;
    }

    @GetMapping("/generateData")
    public ResponseEntity<Purchase> purchaseWithGenerateData() {
        Purchase purchase = TestUtils.generatePurchase();
        log.info("Purchase generated {}", purchase);
        long start = System.currentTimeMillis();
        purchaseService.updatePurchase(purchase);
        TestUtils.elapsedTime(LogSection.REACTIVE, log, start, purchase.getOrderId());
        return ResponseEntity.ok(purchase);
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
