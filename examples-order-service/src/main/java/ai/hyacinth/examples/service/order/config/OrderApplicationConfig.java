package ai.hyacinth.examples.service.order.config;

import ai.hyacinth.core.service.discovery.support.config.DiscoveryConfig;
import ai.hyacinth.core.service.endpoint.support.config.EndpointConfig;
import ai.hyacinth.core.service.jpa.config.JpaConfig;
import ai.hyacinth.examples.service.order.domain.Order;
import ai.hyacinth.examples.service.order.repo.OrderRepo;
import ai.hyacinth.examples.service.order.service.OrderService;
import ai.hyacinth.examples.service.order.web.OrderController;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackageClasses = {Order.class}) // domain-class
@EnableJpaRepositories(basePackageClasses = {OrderRepo.class}) // repo
@ComponentScan(
    basePackageClasses = {OrderService.class, OrderController.class}) // service and controller
@Import({EndpointConfig.class, JpaConfig.class, DiscoveryConfig.class}) // support modules
public class OrderApplicationConfig {}
