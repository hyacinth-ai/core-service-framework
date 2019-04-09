package ai.hyacinth.examples.service.user.config;

import ai.hyacinth.core.service.cache.support.config.CacheConfig;
import ai.hyacinth.core.service.discovery.support.config.DiscoveryConfig;
import ai.hyacinth.core.service.endpoint.support.config.EndpointConfig;
import ai.hyacinth.core.service.jpa.config.JpaConfig;
import ai.hyacinth.examples.service.user.domain.User;
import ai.hyacinth.examples.service.user.repo.UserRepo;
import ai.hyacinth.examples.service.user.service.UserService;
import ai.hyacinth.examples.service.user.web.UserController;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@EntityScan(basePackageClasses = {User.class}) // domain-class
@EnableJpaRepositories(basePackageClasses = {UserRepo.class}) // repo
@ComponentScan(
    basePackageClasses = {
      UserService.class,
      UserController.class,
      WebSecurityConfigurer.class
    }) // service and controller
@Import({
  EndpointConfig.class,
  JpaConfig.class,
  CacheConfig.class,
  DiscoveryConfig.class
}) // support modules
public class UserApplicationConfig {
  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
}
