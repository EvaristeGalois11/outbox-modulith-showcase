package org.example.showcase.event;

import org.springframework.modulith.events.Externalized;

import java.util.UUID;

@Externalized("BAR::bar")
public record FooEvent(UUID id, String name) {
}
