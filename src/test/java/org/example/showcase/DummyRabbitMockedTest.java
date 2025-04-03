package org.example.showcase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.awaitility.Awaitility;
import org.example.showcase.configuration.PostgresConfiguration;
import org.example.showcase.configuration.RabbitMockConfiguration;
import org.example.showcase.entity.Dummy;
import org.example.showcase.event.BarEvent;
import org.example.showcase.listener.BarListener;
import org.example.showcase.repository.BarRepository;
import org.example.showcase.repository.DummyRepository;
import org.example.showcase.service.DummyService;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.events.core.EventPublicationRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.waitAtMost;
import static org.instancio.Select.all;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@DirtiesContext
@ExtendWith(InstancioExtension.class)
@SpringBootTest
@Import({PostgresConfiguration.class, RabbitMockConfiguration.class})
@EnableWireMock(@ConfigureWireMock(baseUrlProperties = "showcase.foo.url"))
class DummyRabbitMockedTest {
    @Autowired
    private DummyService dummyService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DummyRepository dummyRepository;
    @Autowired
    private EventPublicationRepository eventPublicationRepository;
    @Autowired
    private BarRepository barRepository;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private RabbitListenerTestHarness harness;

    @BeforeAll
    static void beforeAll() {
        Awaitility.setDefaultPollInterval(5, TimeUnit.SECONDS);
    }

    @Test
    void dummy() throws JsonProcessingException, InterruptedException {
        BarListener listener = harness.getSpy("bar");
        var answer = harness.getLatchAnswerFor("bar", 1);
        doAnswer(answer).when(listener).onBarEvent(any(BarEvent.class));

        var dummy = Instancio.of(Dummy.class).setBlank(all(Instant.class)).create();
        stubFor(post("/foo").withRequestBody(equalToJson(objectMapper.writeValueAsString(dummy))).willReturn(ok()));
        dummyService.saveDummy(dummy);
        checkDummyIsSaved(dummy);
        waitAtMost(1, TimeUnit.MINUTES)
                .untilAsserted(() -> checkFooEventWasSent(dummy));
        waitAtMost(1, TimeUnit.MINUTES)
                .untilAsserted(this::checkAllEventsAreCompleted);
        waitAtMost(1, TimeUnit.MINUTES)
                .untilAsserted(() -> checkBarIsSaved(dummy));

        answer.await(60);
        Mockito.verify(listener).onBarEvent(any(BarEvent.class));
    }

    private void checkDummyIsSaved(Dummy dummy) {
        var saved = dummyRepository.findAll().getFirst();
        assertThat(saved)
                .hasNoNullFieldsOrProperties()
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

    private void checkBarIsSaved(Dummy dummy) {
        var saved = barRepository.findAll().getFirst();
        assertThat(saved)
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .ignoringFields("timestamp")
                .isEqualTo(dummy);
    }
}
