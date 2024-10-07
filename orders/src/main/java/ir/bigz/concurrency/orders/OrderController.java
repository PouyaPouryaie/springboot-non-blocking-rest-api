package ir.bigz.concurrency.orders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders/v1")
public class OrderController {

    Logger log = LoggerFactory.getLogger(OrderController.class);

    @GetMapping("/{orderId}")
    public ResponseEntity<String> getOrderPrice(@PathVariable String orderId) {
        long leftLimit = 100L;
        long rightLimit = 1000L;
        long generatePrice = leftLimit + (long) (Math.random() * (rightLimit - leftLimit));
        try {
            Thread.sleep(1000);
        } catch (Exception ignore) {

        }
        log.info("The order: [{}] price: [{}]", orderId, generatePrice);
        return ResponseEntity.ok(String.valueOf(generatePrice));
    }
}
