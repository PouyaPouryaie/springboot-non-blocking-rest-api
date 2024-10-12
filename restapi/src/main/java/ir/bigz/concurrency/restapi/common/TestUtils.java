package ir.bigz.concurrency.restapi.common;

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

    public static void elapsedTime(LogSection logSection, Logger log, long start) {
        long finish = System.currentTimeMillis();
        elapsedTime(logSection, log, start, 0);
    }

    public static void elapsedTime(Logger log, long start, long orderId) {
        long finish = System.currentTimeMillis();
        elapsedTime(LogSection.OTHER, log, start, orderId);
    }

    public static void elapsedTime(LogSection logSection, Logger log, long start, long orderId) {
        long finish = System.currentTimeMillis();
        if(orderId == 0) {
            log.info("### {}: BATCH: The time is passed in {} s",logSection.getLogName(), (finish - start) / 1000);
        }else {
            log.info("### {}: orderId: [{}] The time is passed in {} s",logSection.getLogName() ,orderId, (finish - start) / 1000);
        }
    }
}
