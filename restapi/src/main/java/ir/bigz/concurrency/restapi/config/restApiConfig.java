package ir.bigz.concurrency.restapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration(proxyBeanMethods = false)
public class restApiConfig {

//    @Bean
//    public ExecutorService executorService() {
//        ExecutorService executorService;
//        executorService = Executors.newFixedThreadPool(200);
//        return executorService;
//    }

    @Bean
    public ExecutorService executorService() {
        ExecutorService executorService;
        executorService = Executors.newVirtualThreadPerTaskExecutor();
        return executorService;
    }
}
