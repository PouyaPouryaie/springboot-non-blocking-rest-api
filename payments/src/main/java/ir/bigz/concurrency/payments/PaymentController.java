package ir.bigz.concurrency.payments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments/v1")
public class PaymentController {

    Logger log = LoggerFactory.getLogger(PaymentController.class);
    private int paymentId = 1;

    @GetMapping("/{orderId}")
    public ResponseEntity<Payment> getOrderPrice(@PathVariable String orderId) {
        long leftLimit = 1000L;
        long rightLimit = 10000L;
        String generatePrice = leftLimit + (long) (Math.random() * (rightLimit - leftLimit)) + "MD";
        Payment payment = new Payment(String.valueOf(paymentId++), orderId, generatePrice);
        try {
            Thread.sleep(1000);
        } catch (Exception ignore) {

        }
        log.info("### payment paymentId: [{}] orderId: [{}] invoiceNumber: [{}]", payment.paymentId(), payment.orderId(), payment.invoiceNumber());
        return ResponseEntity.ok(payment);
    }
}
