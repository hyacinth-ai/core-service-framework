package ai.hyacinth.core.service.examples.user;

import ai.hyacinth.core.service.examples.user.dto.UserCreationRequest;
import ai.hyacinth.core.service.examples.user.dto.UserInfo;
import ai.hyacinth.core.service.examples.user.service.UserService;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.BDDMockito.given;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserServiceTest {
  @Autowired private UserService userService;

  @Autowired private TestRestTemplate restTemplate;

  @MockBean private RemoteService remoteService;

  //  @Autowired
  //  private WebTestClient webClient;

  @TestConfiguration
  static class TestConfig {}

  @Test
  public void createUser() {
    UserCreationRequest request = new UserCreationRequest("tommy", "123", LocalDate.now());
    UserInfo c = userService.createUser(request);
    Assert.assertEquals(c.getUsername(), request.getUsername());
  }

  @Test
  public void testRemoteClient() {
    given(remoteService.getName()).willReturn("yes");

    Assert.assertThat(remoteService.getName(), Matchers.equalTo("yes"));
  }

  @Test
  public void testRestTemplate() {
    UserInfo userResponse =
        restTemplate.postForObject(
            "/api/users", new UserCreationRequest("hello", "world", LocalDate.now()), UserInfo.class);
    Assert.assertThat(userResponse.getUsername(), Matchers.equalTo("hello"));
  }
}
