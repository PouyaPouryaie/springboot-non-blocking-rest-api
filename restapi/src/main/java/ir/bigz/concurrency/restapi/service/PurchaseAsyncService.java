package ir.bigz.concurrency.restapi.service;

import ir.bigz.concurrency.restapi.dto.BatchPurchase;
import ir.bigz.concurrency.restapi.dto.Payment;
import ir.bigz.concurrency.restapi.dto.Purchase;
import ir.bigz.concurrency.restapi.service.restclient.OrderRestClient;
import ir.bigz.concurrency.restapi.service.restclient.PaymentRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
public class PurchaseAsyncService {

    private final OrderRestClient orderRestClient;
    private final PaymentRestClient paymentRestClient;
    private final ExecutorService executorService;
    private final CopyOnWriteArraySet<Purchase> failedPurchases = new CopyOnWriteArraySet<>();
    Logger log = LoggerFactory.getLogger(PurchaseService.class);

    public PurchaseAsyncService(OrderRestClient orderRestClient, PaymentRestClient paymentRestClient, ExecutorService executorService) {
        this.orderRestClient = orderRestClient;
        this.paymentRestClient = paymentRestClient;
        this.executorService = executorService;
    }

    public void batchUpdatePurchase(BatchPurchase batchPurchase) {
        log.info("Batch Purchase Start for: [{}]", batchPurchase.getNumberOfRequest());
        LongStream.range(0, batchPurchase.getNumberOfRequest())
                .forEach(
                        number -> {
                            number++;
                            Purchase purchase = batchPurchase.getPurchase().clonePurchase(number);
                            updatePurchase(purchase);
                        }
                );
    }

    public void updatePurchase(Purchase purchase) {

        log.info("Purchase Start orderId: [{}]", purchase.getOrderId());
//        CompletableFuture<Void> future = callEndpoint(purchase);
        CompletableFuture<Void> future = callEndpointWithExecutorService(purchase);
        future.join();
        log.info("Purchase Completed orderId: [{}] paymentId: [{}] paymentInvoiceNumber: [{}] price: [{}]",
                purchase.getOrderId(), purchase.getPaymentId(), purchase.getInvoiceNumber(), purchase.getPrice());
    }

    public void updatePurchaseAtFuture(BatchPurchase batchPurchase) {
        List<CompletableFuture<Void>> futurePurchaseList = new ArrayList<>();
        List<Purchase> purchases = new ArrayList<>();
        log.info("Future Purchase Start for: [{}]", batchPurchase.getNumberOfRequest());
        LongStream.range(0, batchPurchase.getNumberOfRequest())
                .forEach(
                        number -> {
                            number++;
                            Purchase purchase = batchPurchase.getPurchase().clonePurchase(number);
                            purchases.add(purchase);
                        }
                );

        purchases.stream().map(this::updatePurchaseAtFuture).forEach(futurePurchaseList::add);
        futurePurchaseList.forEach(CompletableFuture::join);
        purchases.forEach(purchase -> log.info("Purchase Completed orderId: [{}] value is [{}]", purchase.getOrderId(), purchase));
    }

    public void chunkedUpdatePurchase(BatchPurchase batchPurchase) {
        log.info("Batch Purchase Start for: [{}]", batchPurchase.getNumberOfRequest());
        LongStream.range(0, batchPurchase.getNumberOfRequest())
                .forEach(
                        number -> {
                            number++;
                            Purchase purchase = batchPurchase.getPurchase().clonePurchase(number);
                            updatePurchase(purchase);
                        }
                );
    }

    public void updatePurchaseAtFutureChunked(BatchPurchase batchPurchase) {
        List<Purchase> purchases = new ArrayList<>();
        log.info("Future Purchase Start for: [{}]", batchPurchase.getNumberOfRequest());
        LongStream.range(0, batchPurchase.getNumberOfRequest())
                .forEach(
                        number -> {
                            number++;
                            Purchase purchase = batchPurchase.getPurchase().clonePurchase(number);
                            purchases.add(purchase);
                        }
                );

        Collection<List<Purchase>> purchaseGroupList = purchases.stream()
                .collect(Collectors.groupingBy(purchase -> purchase.getOrderId() / 100)).values();
        List<CompletableFuture<Void>> futurePurchaseList = new CopyOnWriteArrayList<>();
        for (List<Purchase> listOfPurchase : purchaseGroupList) {
            listOfPurchase.forEach(purchase -> {
                futurePurchaseList.add(updatePurchaseAtFuture(purchase));
            });
            futurePurchaseList.forEach(CompletableFuture::join);
            futurePurchaseList.clear();
        }

        purchases.forEach(purchase -> log.info("Purchase Completed orderId: [{}] value is [{}]", purchase.getOrderId(), purchase));
    }

    public CompletableFuture<Void> updatePurchaseAtFuture(Purchase purchase) {
        log.info("Purchase Start orderId: [{}]", purchase.getOrderId());
        return callEndpointWithExecutorService(purchase);
    }

    public Set<Purchase> getFailedPurchaseList() {
        log.info("Logging info");
        return failedPurchases;
    }

    private CompletableFuture<Void> callEndpointWithExecutorService(Purchase purchase) {
        return CompletableFuture.allOf(
                CompletableFuture.supplyAsync(() -> paymentRestClient.getPayment(purchase.getOrderId()), executorService)
                        .thenAccept(payment -> {
                            purchase.setInvoiceNumber(payment.invoiceNumber());
                            purchase.setPaymentId(payment.paymentId());
                        })
//                        .orTimeout(5, TimeUnit.SECONDS)
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
                CompletableFuture.supplyAsync(() -> orderRestClient.getPrice(purchase.getOrderId()), executorService)
                        .thenAccept(purchase::setPrice)
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
        );
    }

    private CompletableFuture<Void> callEndpoint(Purchase purchase) {

        CompletableFuture<Payment> paymentAsync = CompletableFuture.supplyAsync(() -> paymentRestClient.getPayment(purchase.getOrderId()))
                .orTimeout(5, TimeUnit.SECONDS)
                .handle((result, exception) -> {
                    if (Objects.nonNull(exception)) {
                        if (Objects.requireNonNull(exception) instanceof TimeoutException) {
                            log.error("Exception: [Payment Timeout exception]");
                            failedPurchases.add(purchase);
                        }
                        log.error("Exception: [Payment: {}]", exception.getMessage());
                        failedPurchases.add(purchase);
                        return null;
                    } else {
                        purchase.setInvoiceNumber(result.invoiceNumber());
                        purchase.setPaymentId(result.paymentId());
                        return result;
                    }
                });

        CompletableFuture<Void> orderAsync = CompletableFuture.supplyAsync(() -> orderRestClient.getPrice(purchase.getOrderId()))
                .thenAccept(purchase::setPrice)
                .orTimeout(5, TimeUnit.SECONDS)
                .exceptionally((exception) -> {
                        if (Objects.requireNonNull(exception) instanceof TimeoutException) {
                            log.error("Exception: [Order Timeout exception]");
                            failedPurchases.add(purchase);
                        }
                        log.error("Exception: [Order: {}]", exception.getMessage());
                        failedPurchases.add(purchase);
                        return null;
                });

        return CompletableFuture.allOf(paymentAsync, orderAsync);
    }
}
