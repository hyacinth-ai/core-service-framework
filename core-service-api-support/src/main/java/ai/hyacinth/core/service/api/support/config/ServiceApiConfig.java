package ai.hyacinth.core.service.api.support.config;

import feign.Logger;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class ServiceApiConfig {
  @Bean
  public ErrorDecoder errorDecoder() {
    return new ServiceApiErrorDecoder();
  }

  @Bean
  public Logger.Level loggerLevel() {
    return Logger.Level.FULL;
  }

  @Bean
  public Retryer retryer() {
    return Retryer.NEVER_RETRY; // Retryer.Default();
  }
}
