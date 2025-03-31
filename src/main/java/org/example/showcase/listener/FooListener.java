package org.example.showcase.listener;

import org.example.showcase.event.FooEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class FooListener {
    private static final Logger log = LoggerFactory.getLogger(FooListener.class);

    @ApplicationModuleListener
    public void onFooEvent(FooEvent event) {
        log.info("Foo event: {}", event);
    }
}
