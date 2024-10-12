# Spring boot - Concurrency
This project show how to use different apporach to implement non-blocking for restAPI

# Concurrency

1. first attempt
   - ues completable future and call endpoint concurrent.
   - define list of completable future and then call all of them
     - I got this error when I call batch for more than 100 request concurrently <br>
     by completable future list. <br>
       `java.util.concurrent.RejectedExecutionException: Thread limit exceeded replacing blocked worker`
2. change tomcat thread pool on order and payment module
   - nothing change related to previous error
3. third attempt
   - I define a list that contains list of completable future which is needed to complete in future<br>
   - with this approach each time I just called a specific amount of request
4. use webClient
   - I use web client to have non-blocking client to send the request
   - implement reactive approach to call endpoint and get data
5. define own executor and use virtualThread
   - Improve performance and reduce time for response
   - Note: It's better to use chunked, I Implemented at `PurchaseAsyncService`
6. It is interesting:
   - when I set max heap 250mb and use parallel GC
     - I was able to call 1000 request concurrent on reactive
     - but I got memory error when I wanted to use async (completable future)
```bash
 java -Xms256m -Xmx512m -XX:+UseParallelGC -jar restapi-0.0.1.jar
```

## Tool Set
- `Apache Bench` is an open source load testing utility that comes bundled with the Apache web server.
```bash
apt-get install apache2-utils
ab -n 10000 -c 10 http://localhost:8080/actuator/metrics
```
- `Instancio` is a Java library for generating test objects. Its main goal is to reduce manual data setup in unit tests <br>
[Generate Test Data Using Instancio](https://www.baeldung.com/java-test-data-instancio)

## Useful Source
- [Batch Processing of Stream Data in Java](https://www.baeldung.com/java-stream-batch-processing)
- [CompletableFuture API](https://medium.com/@AlexanderObregon/javas-completablefuture-api-deep-dive-fecbdd78c07d)
- [how to use jmeter to read json file](https://dzone.com/articles/how-to-use-the-json-plugin-in-jmeter)
- [jvm parameter](https://www.baeldung.com/jvm-parameters)