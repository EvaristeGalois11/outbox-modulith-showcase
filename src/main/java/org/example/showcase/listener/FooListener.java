package org.example.showcase.listener;

import org.example.showcase.event.FooEvent;
import org.example.showcase.service.DisruptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class FooListener {
    private static final Logger log = LoggerFactory.getLogger(FooListener.class);

    private final RestClient restClient;
    private final DisruptionService disruptionService;

    public FooListener(RestClient restClient, DisruptionService disruptionService) {
        this.restClient = restClient;
        this.disruptionService = disruptionService;
    }

    @ApplicationModuleListener
    public void onFooEvent(FooEvent event) {
        log.info("Sending Foo event: {}", event);
        disruptionService.disrupt("foo");
        var response = restClient
                .post()
                .uri("/foo")
                .body(event)
                .retrieve()
                .toBodilessEntity();
        log.info("Foo response status: {}", response.getStatusCode().value());
    }
}
