package ai.hyacinth.core.service.endpoint.support.config;

import ai.hyacinth.core.service.endpoint.support.errorhandler.ServiceControllerExceptionHandler;
import ai.hyacinth.core.service.swagger.support.SwaggerConfig;
import ai.hyacinth.core.service.web.support.config.WebConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

@Configuration
@Import({SwaggerConfig.class, WebConfig.class, SchedulingConfig.class})
@ComponentScan(basePackageClasses = ServiceControllerExceptionHandler.class)
public class EndpointConfig {
  @ConditionalOnMissingBean
  @LoadBalanced
  @Bean
  RestTemplate loadBalancedRestTemplate() {
    return new RestTemplate();
  }

  //  @Bean
  //  RestTemplate restTemplate() {
  //    return new RestTemplate();
  //  }
}
