package ir.bigz.concurrency.restapi.service.webclient;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class OrderWebClient {

    private final WebClient client;

    public OrderWebClient() {
        client = WebClient.builder()
                .baseUrl("http://localhost:9091/orders/v1")
                .build();
    }

//    public Mono<String> getPrice(long orderId) {
//        WebClient.ResponseSpec retrieve = client.get()
//                .uri("/" + orderId)
//                .accept(MediaType.APPLICATION_JSON).retrieve();
//        return retrieve.bodyToMono(String.class);
//    }

    public Mono<ResponseEntity<String>> getPrice(long orderId) {
        return client.get()
                .uri("/" + orderId)
                .accept(MediaType.APPLICATION_JSON).retrieve()
                .onStatus(httpStatusCode -> !httpStatusCode.is2xxSuccessful(), clientResponse -> Mono.empty())
                .toEntity(String.class)
                .onErrorResume(ignore -> Mono.empty());
    }
}
