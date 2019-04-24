package ai.hyacinth.examples.service.debug.config;

import ai.hyacinth.core.service.bus.support.config.BusConfig;
import ai.hyacinth.core.service.discovery.support.config.DiscoveryConfig;
import ai.hyacinth.core.service.endpoint.support.config.EndpointConfig;
import ai.hyacinth.core.service.jpa.config.JpaConfig;
import ai.hyacinth.examples.service.debug.domain.ApiCallHistory;
import ai.hyacinth.examples.service.debug.repo.ApiCallHistoryRepo;
import ai.hyacinth.examples.service.debug.service.DebugService;
import ai.hyacinth.examples.service.debug.web.DebugController;
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
