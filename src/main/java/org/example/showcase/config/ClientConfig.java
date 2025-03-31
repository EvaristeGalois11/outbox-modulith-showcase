package org.example.showcase.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {
    @Bean
    public RestClient restClient(RestClient.Builder builder, @Value("${showcase.foo.url}") String fooUrl) {
        return builder
                .baseUrl(fooUrl)
                .build();
    }
}
