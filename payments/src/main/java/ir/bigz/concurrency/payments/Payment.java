package ir.bigz.concurrency.payments;

public record Payment(String paymentId, String orderId, String invoiceNumber) {
}
