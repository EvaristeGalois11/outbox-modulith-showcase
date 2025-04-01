package org.example.showcase.listener;

import org.example.showcase.entity.Bar;
import org.example.showcase.event.BarEvent;
import org.example.showcase.repository.BarRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class BarListener {
    private static final Logger log = LoggerFactory.getLogger(BarListener.class);

    private final BarRepository barRepository;

    public BarListener(BarRepository barRepository) {
        this.barRepository = barRepository;
    }

    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(name = "BAR"), value = @Queue(name = "bar"), key = "bar"))
    public void onBarEvent(@Payload BarEvent event) {
        log.info("Received bar event: {}", event);
        Bar bar = new Bar();
        bar.setId(event.id());
        bar.setName(event.name());
        barRepository.save(bar);
    }
}
