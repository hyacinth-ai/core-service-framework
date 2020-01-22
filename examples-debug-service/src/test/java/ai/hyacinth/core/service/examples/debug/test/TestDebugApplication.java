package ai.hyacinth.core.service.examples.debug.test;

import ai.hyacinth.core.service.examples.debug.boot.DebugApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DebugApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
// @AutoConfigureWebTestClient
public class TestDebugApplication {
  @Autowired private WebTestClient client;

  @Autowired private TestRestTemplate restTemplate;

  @Test
  public void testAddr() {
    client.get().uri("/api/addr")
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody()
        .jsonPath("$.localhost.address")
        .exists() // .isEqualTo("127.0.0.1")
        ;
  }
}
