package ir.bigz.concurrency.restapi.service.webclient;

import ir.bigz.concurrency.restapi.dto.Payment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PaymentWebClient {

    private final WebClient client;

    public PaymentWebClient() {
        this.client = WebClient.builder()
                .baseUrl("http://localhost:9092/payments/v1")
                .build();
    }

//    public Mono<Payment> getPayment(long orderId) {
//        WebClient.ResponseSpec retrieve = client.get()
//                .uri("/" + orderId)
//                .accept(MediaType.APPLICATION_JSON).retrieve();
//
//        return retrieve.bodyToMono(Payment.class);
//    }

    public Mono<ResponseEntity<Payment>> getPayment(long orderId) {

        return client.get()
                .uri("/" + orderId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(httpStatusCode -> !httpStatusCode.is2xxSuccessful(), clientResponse -> Mono.empty())
                .toEntity(Payment.class)
                .onErrorResume(ignore -> Mono.empty());
    }
}
