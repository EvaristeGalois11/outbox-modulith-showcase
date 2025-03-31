package org.example.showcase.service;

import org.example.showcase.entity.Dummy;
import org.example.showcase.event.FooEvent;
import org.example.showcase.repository.DummyRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DummyService {
    private final DummyRepository dummyRepository;
    private final ApplicationEventPublisher eventPublisher;

    public DummyService(DummyRepository dummyRepository, ApplicationEventPublisher eventPublisher) {
        this.dummyRepository = dummyRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void saveDummy(Dummy dummy) {
        dummy = dummyRepository.save(dummy);
        eventPublisher.publishEvent(new FooEvent(dummy.getId(), dummy.getName()));
    }
}
