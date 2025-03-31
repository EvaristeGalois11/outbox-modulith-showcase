package org.example.showcase.controller;

import org.example.showcase.service.DummyService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("dummy")
public class DummyController {
    private final DummyService dummyService;

    public DummyController(DummyService dummyService) {
        this.dummyService = dummyService;
    }

    @PostMapping
    public long createDummy() {
        return dummyService.createDummy();
    }
}
