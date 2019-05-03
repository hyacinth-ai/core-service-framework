package ai.hyacinth.core.service.trigger.server.boot;

import ai.hyacinth.core.service.trigger.server.config.TriggerApplicationConfig;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@Configuration
@EnableScheduling
@EnableAsync
@Import(TriggerApplicationConfig.class)
public class TriggerApplication {
  public static void main(String[] args) {
    new SpringApplicationBuilder(TriggerApplication.class)
        .web(WebApplicationType.SERVLET)
        .run(args);
  }
}
