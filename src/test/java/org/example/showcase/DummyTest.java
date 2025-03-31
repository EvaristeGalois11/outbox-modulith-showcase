package org.example.showcase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.showcase.configuration.TestcontainersConfiguration;
import org.example.showcase.entity.Dummy;
import org.example.showcase.repository.DummyRepository;
import org.example.showcase.service.DummyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.events.core.EventPublicationRepository;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

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
    void dummy() throws JsonProcessingException, InterruptedException {
        var dummy = randomDummy();
        stubFor(post("/foo").withRequestBody(equalToJson(objectMapper.writeValueAsString(dummy))).willReturn(ok()));
        dummyService.saveDummy(dummy);
        Thread.sleep(1000);
        checkDummyIsSaved(dummy);
        checkFooEventWasSent(dummy);
        checkAllEventsAreCompleted();
    }

    private void checkDummyIsSaved(Dummy dummy) {
        var saved = dummyRepository.findAll().getFirst();
        assertThat(saved).hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .ignoringFields("timestamp")
                .isEqualTo(dummy);
    }

    private void checkFooEventWasSent(Dummy dummy) throws JsonProcessingException {
        verify(postRequestedFor(urlEqualTo("/foo")).withRequestBody(equalToJson(objectMapper.writeValueAsString(dummy))));
    }

    private void checkAllEventsAreCompleted() {
        assertThat(eventPublicationRepository.findIncompletePublications()).isEmpty();
    }

    private Dummy randomDummy() {
        Dummy dummy = new Dummy();
        dummy.setId(UUID.randomUUID());
        dummy.setName("dummy");
        return dummy;
    }
}

