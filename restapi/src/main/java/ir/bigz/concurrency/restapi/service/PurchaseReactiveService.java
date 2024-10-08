package ir.bigz.concurrency.restapi.service;

import ir.bigz.concurrency.restapi.dto.Payment;
import ir.bigz.concurrency.restapi.dto.Purchase;
import ir.bigz.concurrency.restapi.service.webclient.OrderWebClient;
import ir.bigz.concurrency.restapi.service.webclient.PaymentWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class PurchaseReactiveService {

    Logger log = LoggerFactory.getLogger(PurchaseReactiveService.class);

    private final PaymentWebClient paymentService;
    private final OrderWebClient orderService;
    private final CopyOnWriteArraySet<Purchase> failedPurchases = new CopyOnWriteArraySet<>();

    public PurchaseReactiveService(PaymentWebClient paymentService, OrderWebClient orderService) {
        this.paymentService = paymentService;
        this.orderService = orderService;
    }

    public void updatePurchase(Purchase purchase) {
        log.info("Purchase Start orderId: [{}]", purchase.getOrderId());
        var isFailed = false;

        Mono<ResponseEntity<Payment>> monoPayment = paymentService.getPayment(purchase.getOrderId());
        Mono<ResponseEntity<String>> monoOrder = orderService.getPrice(purchase.getOrderId());
        ResponseEntity<Payment> responsePayment = monoPayment.block();

        if(Objects.nonNull(responsePayment) && responsePayment.getStatusCode().is2xxSuccessful()) {
            purchase.setPaymentId(Objects.requireNonNull(responsePayment.getBody()).paymentId());
            purchase.setInvoiceNumber(responsePayment.getBody().invoiceNumber());
        } else {
            failedPurchases.add(purchase);
            isFailed = true;
        }

        ResponseEntity<String> responseOrder = monoOrder.block();

        if(Objects.nonNull(responseOrder) && responseOrder.getStatusCode().is2xxSuccessful()) {
            purchase.setPrice(Objects.requireNonNull(responseOrder.getBody()));
        } else {
            failedPurchases.add(purchase);
            isFailed = true;
        }

        if(isFailed) {
            log.info("Purchase Failed orderId: [{}] paymentId: [{}] paymentInvoiceNumber: [{}] price: [{}]",
                    purchase.getOrderId(), purchase.getPaymentId(), purchase.getInvoiceNumber(), purchase.getPrice());
        }else {
            log.info("Purchase Completed orderId: [{}] paymentId: [{}] paymentInvoiceNumber: [{}] price: [{}]",
                    purchase.getOrderId(), purchase.getPaymentId(), purchase.getInvoiceNumber(), purchase.getPrice());
        }

    }

    public Set<Purchase> getFailedPurchaseList() {
        log.info("Logging info");
        return failedPurchases;
    }
}
