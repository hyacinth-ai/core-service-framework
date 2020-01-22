package ai.hyacinth.core.service.examples.debug.boot;

import ai.hyacinth.core.service.examples.debug.config.DebugApplicationConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.validation.annotation.Validated;

@SpringBootApplication
@Import({DebugApplicationConfig.class})
@Slf4j
public class DebugApplication {
  @Autowired
  private ApplicationContext ctx;

  public static void main(String[] args) {
    new SpringApplicationBuilder(DebugApplication.class)
        .web(WebApplicationType.SERVLET)
        .lazyInitialization(true)
        .bannerMode(Banner.Mode.OFF)
        .run(args);
  }
}
