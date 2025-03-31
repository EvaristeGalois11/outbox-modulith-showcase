package org.example.showcase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.showcase.configuration.TestcontainersConfiguration;
import org.example.showcase.entity.Dummy;
import org.example.showcase.repository.DummyRepository;
import org.example.showcase.service.DummyService;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.events.core.EventPublicationRepository;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.instancio.Select.all;

@ExtendWith(InstancioExtension.class)
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@EnableWireMock(@ConfigureWireMock(baseUrlProperties = "showcase.foo.url"))
class DummyTest {
    @Autowired
    private DummyService dummyService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DummyRepository dummyRepository;
    @Autowired
    private EventPublicationRepository eventPublicationRepository;

    @Test
    void dummy() throws JsonProcessingException {
        var dummy = Instancio.of(Dummy.class).setBlank(all(Instant.class)).create();
        stubFor(post("/foo").withRequestBody(equalToJson(objectMapper.writeValueAsString(dummy))).willReturn(ok()));
        dummyService.saveDummy(dummy);
        checkDummyIsSaved(dummy);
        await()
                .atLeast(20, TimeUnit.SECONDS)
                .atMost(1, TimeUnit.MINUTES)
                .pollInterval(5, TimeUnit.SECONDS)
                .untilAsserted(() -> checkFooEventWasSent(dummy));
        await()
                .atMost(1, TimeUnit.MINUTES)
                .pollInterval(5, TimeUnit.SECONDS)
                .untilAsserted(this::checkAllEventsAreCompleted);
    }

    private void checkDummyIsSaved(Dummy dummy) {
        var saved = dummyRepository.findAll().getFirst();
        assertThat(saved).hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .ignoringFields("timestamp")
                .isEqualTo(dummy);
    }

    private void checkFooEventWasSent(Dummy dummy) throws JsonProcessingException {
        verify(1, postRequestedFor(urlEqualTo("/foo")).withRequestBody(equalToJson(objectMapper.writeValueAsString(dummy))));
    }

    private void checkAllEventsAreCompleted() {
        assertThat(eventPublicationRepository.findIncompletePublications()).isEmpty();
    }
}

