package ir.bigz.concurrency.restapi.service;

import ir.bigz.concurrency.restapi.dto.Payment;
import ir.bigz.concurrency.restapi.dto.Purchase;
import ir.bigz.concurrency.restapi.service.webclient.OrderWebClient;
import ir.bigz.concurrency.restapi.service.webclient.PaymentWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
        CompletableFuture.allOf(
                CompletableFuture.supplyAsync(() -> paymentService.getPayment(purchase.getOrderId()))
                        .thenAccept(mono -> {
                            Payment payment = mono.block();
                            purchase.setInvoiceNumber(payment.invoiceNumber());
                            purchase.setPaymentId(payment.paymentId());
                        })
                        .orTimeout(5, TimeUnit.SECONDS)
                        .handle((result, exception) -> {
                            if (Objects.nonNull(exception)) {
                                if (Objects.requireNonNull(exception) instanceof TimeoutException) {
                                    log.error("Payment Timeout exception: [{}]", exception.getMessage());
                                    failedPurchases.add(purchase);
                                    return null;
                                }
                                log.error("Payment has Exception: [{}]", exception.getMessage());
                                failedPurchases.add(purchase);
                                return null;
                            }
                            return result;
                        }),
                CompletableFuture.supplyAsync(() -> orderService.getPrice(purchase.getOrderId()))
                        .thenAccept(stringMono -> {
                            purchase.setPrice(stringMono.block());
                        })
                        .handle((result, exception) -> {
                            if (Objects.nonNull(exception)) {
                                if (Objects.requireNonNull(exception) instanceof TimeoutException) {
                                    log.error("Order Timeout exception: [{}]", exception.getMessage());
                                    failedPurchases.add(purchase);
                                    return null;
                                }
                                log.error("Order has Exception: [{}]", exception.getMessage());
                                failedPurchases.add(purchase);
                                return null;
                            }
                            return result;
                        })
        ).join();


        log.info("Purchase Completed orderId: [{}] paymentId: [{}] paymentInvoiceNumber: [{}] price: [{}]",
                purchase.getOrderId(), purchase.getPaymentId(), purchase.getInvoiceNumber(), purchase.getPrice());
    }
}
