package ir.bigz.concurrency.restapi.service.restclient;

import ir.bigz.concurrency.restapi.dto.Payment;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class PaymentRestClient {

    private final RestClient client;

    public PaymentRestClient() {
        client = RestClient.builder()
                .baseUrl("http://localhost:9092/payments/v1")
                .build();
    }

    public Payment getPayment(long orderId) {
        return client.get()
                .uri("/" + orderId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RestClientException(response.getStatusText());
                }))
                .body(Payment.class);
    }
}
