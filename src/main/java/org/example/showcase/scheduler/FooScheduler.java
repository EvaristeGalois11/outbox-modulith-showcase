package org.example.showcase.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.IncompleteEventPublications;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class FooScheduler {
    private static final Logger log = LoggerFactory.getLogger(FooScheduler.class);
    private final IncompleteEventPublications incompleteEventPublications;

    public FooScheduler(IncompleteEventPublications incompleteEventPublications) {
        this.incompleteEventPublications = incompleteEventPublications;
    }

    @Scheduled(fixedDelay = 2000)
    public void schedule() {
        log.info("Resubmitting incomplete events");
        incompleteEventPublications.resubmitIncompletePublicationsOlderThan(Duration.ofSeconds(5));
    }
}
