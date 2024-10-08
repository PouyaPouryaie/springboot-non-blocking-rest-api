package ir.bigz.concurrency.restapi.service.restclient;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class OrderRestClient {

    private final RestClient client;

    public OrderRestClient() {
        client = RestClient.builder()
                .baseUrl("http://localhost:9091/orders/v1")
                .build();
    }

    public String getPrice(long orderId) {
        return client.get()
                .uri("/" + orderId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RestClientException(response.getStatusText());
                }))
                .body(String.class);
    }
}
