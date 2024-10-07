package ir.bigz.concurrency.restapi.service.restclient;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class OrderRestClient {

    private final RestClient client;

    public OrderRestClient() {
        client = RestClient.builder()
                .baseUrl("http://localhost:9091/orders/v1")
                .build();
    }

    public String getPrice(long orderId) {
        String price = client.get()
                .uri("/" + orderId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);
        return price;
    }
}
