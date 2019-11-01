package ai.hyacinth.core.service.examples.debug.config;

import ai.hyacinth.core.service.cache.support.config.CacheConfig;
import ai.hyacinth.core.service.endpoint.support.config.EndpointConfig;
import ai.hyacinth.core.service.examples.debug.domain.ApiCallHistory;
import ai.hyacinth.core.service.examples.debug.repo.ApiCallHistoryRepo;
import ai.hyacinth.core.service.examples.debug.service.DebugService;
import ai.hyacinth.core.service.examples.debug.web.DebugController;
import ai.hyacinth.core.service.jpa.config.JpaConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackageClasses = {ApiCallHistory.class}) // domain-class
@EnableJpaRepositories(basePackageClasses = {ApiCallHistoryRepo.class}) // repo
@ComponentScan(
    basePackageClasses = {DebugService.class, DebugController.class}) // service and controller
@Import({
  EndpointConfig.class,
  JpaConfig.class,
  CacheConfig.class,
}) // support modules
@Slf4j
public class DebugApplicationConfig {}
