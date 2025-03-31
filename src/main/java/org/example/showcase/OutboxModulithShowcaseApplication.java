package org.example.showcase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OutboxModulithShowcaseApplication {
    public static void main(String[] args) {
        SpringApplication.run(OutboxModulithShowcaseApplication.class, args);
    }
}
