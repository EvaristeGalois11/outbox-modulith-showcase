package org.example.showcase.config;

import org.example.showcase.event.BarEvent;
import org.example.showcase.event.FooEvent;
import org.example.showcase.service.DisruptionService;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.modulith.events.EventExternalizationConfiguration;

@Configuration
public class RoutingConfiguration {

    @Bean
    public EventExternalizationConfiguration eventExternalizationConfiguration(DisruptionService disruptionService) {
        return EventExternalizationConfiguration.externalizing()
                .select(EventExternalizationConfiguration.annotatedAsExternalized())
                .mapping(FooEvent.class, foo -> disruptionService.disrupt("bar", new BarEvent(foo.id(), foo.name())))
                .build();
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
}
