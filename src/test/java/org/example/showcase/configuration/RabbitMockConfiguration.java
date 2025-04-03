package org.example.showcase.configuration;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.amqp.rabbit.test.TestRabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

@TestConfiguration
@RabbitListenerTest
public class RabbitMockConfiguration {
    @Bean
    public TestRabbitTemplate template(Jackson2JsonMessageConverter converter) {
        var testRabbitTemplate = new TestRabbitTemplate(connectionFactory());
        testRabbitTemplate.setMessageConverter(converter);
        return testRabbitTemplate;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactory factory = mock(RETURNS_DEEP_STUBS);
        when(factory.createConnection().createChannel(anyBoolean()).isOpen()).thenReturn(true);
        return factory;
    }
}
