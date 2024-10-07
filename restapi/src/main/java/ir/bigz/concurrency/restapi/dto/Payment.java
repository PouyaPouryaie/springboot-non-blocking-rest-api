package ir.bigz.concurrency.restapi.dto;

public record Payment(String paymentId, String orderId, String invoiceNumber) {
}
