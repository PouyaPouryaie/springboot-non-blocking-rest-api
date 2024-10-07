package ir.bigz.concurrency.restapi.dto;

import lombok.Builder;
import lombok.ToString;

import java.util.Objects;

@ToString
@Builder
public class Purchase {
    final String orderDescription;
    final String paymentDescription;
    final String buyerName;
    final long orderId;
    String paymentId;
    String invoiceNumber;
    String price;

    public Purchase(String orderDescription,
                    String paymentDescription,
                    String buyerName,
                    long orderId,
                    String paymentId,
                    String invoiceNumber,
                    String price) {
        this.orderDescription = orderDescription;
        this.paymentDescription = paymentDescription;
        this.buyerName = buyerName;
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.invoiceNumber = invoiceNumber;
        this.price = price;
    }

    public String getOrderDescription() {
        return orderDescription;
    }

    public String getPaymentDescription() {
        return paymentDescription;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public long getOrderId() {
        return orderId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getPrice() {
        return price;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Purchase clonePurchase(long orderId) {
        return new Purchase(orderDescription, paymentDescription, buyerName, orderId, paymentId, invoiceNumber, price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Purchase purchase)) return false;
        return Objects.equals(orderDescription, purchase.orderDescription) &&
                Objects.equals(paymentDescription, purchase.paymentDescription) &&
                Objects.equals(buyerName, purchase.buyerName) &&
                Objects.equals(orderId, purchase.orderId) &&
                Objects.equals(paymentId, purchase.paymentId) &&
                Objects.equals(invoiceNumber, purchase.invoiceNumber) &&
                Objects.equals(price, purchase.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderDescription, paymentDescription, buyerName, orderId, paymentId, invoiceNumber, price);
    }
}
