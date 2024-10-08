package ir.bigz.concurrency.restapi.service;

import ir.bigz.concurrency.restapi.dto.BatchPurchase;
import ir.bigz.concurrency.restapi.dto.Payment;
import ir.bigz.concurrency.restapi.dto.Purchase;
import ir.bigz.concurrency.restapi.service.restclient.OrderRestClient;
import ir.bigz.concurrency.restapi.service.restclient.PaymentRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.LongStream;

@Service
public class PurchaseService {

    private final OrderRestClient orderRestClient;
    private final PaymentRestClient paymentRestClient;
    private final CopyOnWriteArraySet<Purchase> failedPurchases = new CopyOnWriteArraySet<>();
    Logger log = LoggerFactory.getLogger(PurchaseService.class);

    public PurchaseService(OrderRestClient orderRestClient, PaymentRestClient paymentRestClient) {
        this.orderRestClient = orderRestClient;
        this.paymentRestClient = paymentRestClient;
    }

    public void updatePurchase(Purchase purchase) {
        log.info("Purchase Start orderId: [{}]", purchase.getOrderId());
        try {
            Payment payment = paymentRestClient.getPayment(purchase.getOrderId());
            String price = orderRestClient.getPrice(purchase.getOrderId());
            purchase.setInvoiceNumber(payment.invoiceNumber());
            purchase.setPaymentId(payment.paymentId());
            purchase.setPrice(price);
        } catch (Exception exception) {
            log.error("Exception: Normal Purchase: {}", exception.getMessage());
            failedPurchases.add(purchase);
        }

        log.info("Purchase Completed orderId: [{}] paymentId: [{}] paymentInvoiceNumber: [{}] price: [{}]",
                purchase.getOrderId(), purchase.getPaymentId(), purchase.getInvoiceNumber(), purchase.getPrice());
    }

    public void batchUpdatePurchase(BatchPurchase batchPurchase) {
        log.info("Batch Purchase Start for: [{}]", batchPurchase.getNumberOfRequest());
        LongStream.range(1, batchPurchase.getNumberOfRequest())
                .forEach(
                        number -> {
                            number++;
                            Purchase purchase = batchPurchase.getPurchase().clonePurchase(number);
                            updatePurchase(purchase);
                        }
                );
    }

    public Set<Purchase> getFailedPurchaseList() {
        log.info("Logging info");
        return failedPurchases;
    }
}
