package ir.bigz.concurrency.restapi.dto;

import lombok.ToString;

@ToString
public class BatchPurchase {
    long numberOfRequest;
    Purchase purchase;

    public BatchPurchase() {
    }

    public BatchPurchase(long numberOfRequest, Purchase purchase) {
        this.numberOfRequest = numberOfRequest;
        this.purchase = purchase;
    }

    public long getNumberOfRequest() {
        return numberOfRequest;
    }

    public void setNumberOfRequest(long numberOfRequest) {
        this.numberOfRequest = numberOfRequest;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }
}
