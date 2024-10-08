package ir.bigz.concurrency.restapi.exception;

public class RestClientException extends RuntimeException {

    public RestClientException(String message) {
        super(message);
    }
}
