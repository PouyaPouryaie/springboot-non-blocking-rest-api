package ir.bigz.concurrency.restapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class ContextClosedEventListener {

    Logger log = LoggerFactory.getLogger(ContextClosedEventListener.class);
    private final ExecutorService executorService;

    public ContextClosedEventListener(ExecutorService executorService) {
        this.executorService = executorService;
    }

    /*
        Implement graceful shutdown procedures for ExecutorServices
        to ensure all tasks are completed before application termination
     */
    @EventListener(ContextClosedEvent.class)
    public void onContextClosedEvent(ContextClosedEvent event) {

        try {
            executorService.close();
            executorService.shutdown();
            boolean tasksCompleted = executorService.awaitTermination(30, TimeUnit.SECONDS);
            if (tasksCompleted) {
                log.info("ContextClosedEvent: All tasks completed");
            } else {
                log.warn("ContextClosedEvent: Some tasks did not complete in 30 seconds, forcing shutdown");
                executorService.shutdownNow();
                executorService.awaitTermination(0, TimeUnit.SECONDS);
            }
        } catch (InterruptedException exception) {
            log.warn("ContextClosedEvent: Interrupted while waiting for tasks to terminate", exception);
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        } finally {
            LocalTime currentTime = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            log.info("ContextClosedEvent: executor is finished at: {}", currentTime.format(formatter));
        }
    }
}
