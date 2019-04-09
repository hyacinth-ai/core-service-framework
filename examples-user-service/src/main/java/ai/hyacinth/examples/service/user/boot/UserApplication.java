package ai.hyacinth.examples.service.user.boot;

import ai.hyacinth.examples.service.user.config.UserApplicationConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({UserApplicationConfig.class})
@Slf4j
public class UserApplication implements ApplicationListener<ApplicationReadyEvent> {

  public static void main(String[] args) {
    SpringApplication.run(UserApplication.class, args);
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    String testKey = event.getApplicationContext().getEnvironment().getProperty("test.key");
    String testKey2 = event.getApplicationContext().getEnvironment().getProperty("test.key2");
    log.info("testKey is {} {}", testKey, testKey2);
  }
}
