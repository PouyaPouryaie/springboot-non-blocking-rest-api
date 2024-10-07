package ir.bigz.concurrency.restapi;

import ir.bigz.concurrency.restapi.dto.Purchase;
import org.instancio.Instancio;
import org.instancio.Select;
import org.slf4j.Logger;

public class TestUtils {

    public static Purchase generatePurchase() {
        return Instancio.of(Purchase.class)
                .ignore(Select.field(Purchase::getPaymentId))
                .ignore(Select.field(Purchase::getPrice))
                .ignore(Select.field(Purchase::getInvoiceNumber))
                .create();
    }

    public static void elapsedTime(Logger log, long start, long orderId) {
        long finish = System.currentTimeMillis();
        log.info("### orderId: [{}] The time is passed in {} ms",orderId, finish - start);
    }
}
