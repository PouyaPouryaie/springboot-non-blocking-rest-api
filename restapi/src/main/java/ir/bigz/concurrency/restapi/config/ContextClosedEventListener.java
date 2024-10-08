package ir.bigz.concurrency.restapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
public class ContextClosedEventListener {

    Logger log = LoggerFactory.getLogger(ContextClosedEventListener.class);
    private final ExecutorService executorService;

    public ContextClosedEventListener(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @EventListener(ContextClosedEvent.class)
    public void onContextClosedEvent(ContextClosedEvent event) {
        executorService.close();
        executorService.shutdown();
        log.info("ContextClosedEvent occurred at millis: {}", event.getTimestamp());
    }
}
