package ai.hyacinth.core.service.gateway.server.boot;

import ai.hyacinth.core.service.gateway.server.config.GatewayConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({GatewayConfig.class})
@Slf4j
public class GatewayServerApplication {
  public static void main(String[] args) {
    SpringApplication.run(GatewayServerApplication.class, args);
  }
}
