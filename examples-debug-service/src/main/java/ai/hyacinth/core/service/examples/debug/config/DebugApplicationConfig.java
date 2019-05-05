package ai.hyacinth.core.service.examples.debug.config;

import ai.hyacinth.core.service.bus.support.config.BusConfig;
import ai.hyacinth.core.service.discovery.support.config.DiscoveryConfig;
import ai.hyacinth.core.service.endpoint.support.config.EndpointConfig;
import ai.hyacinth.core.service.examples.debug.web.DebugController;
import ai.hyacinth.core.service.jpa.config.JpaConfig;
import ai.hyacinth.core.service.examples.debug.domain.ApiCallHistory;
import ai.hyacinth.core.service.examples.debug.repo.ApiCallHistoryRepo;
import ai.hyacinth.core.service.examples.debug.service.DebugService;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackageClasses = {ApiCallHistory.class}) // domain-class
@EnableJpaRepositories(basePackageClasses = {ApiCallHistoryRepo.class}) // repo
@ComponentScan(
    basePackageClasses = {DebugService.class, DebugController.class}) // service and controller
@Import({EndpointConfig.class, JpaConfig.class, DiscoveryConfig.class, BusConfig.class}) // support modules
public class DebugApplicationConfig {}
