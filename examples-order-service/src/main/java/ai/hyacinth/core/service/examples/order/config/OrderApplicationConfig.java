package ai.hyacinth.core.service.examples.order.config;

import ai.hyacinth.core.service.discovery.support.config.DiscoveryConfig;
import ai.hyacinth.core.service.endpoint.support.config.EndpointConfig;
import ai.hyacinth.core.service.examples.order.domain.Order;
import ai.hyacinth.core.service.examples.order.repo.OrderRepo;
import ai.hyacinth.core.service.examples.order.service.OrderService;
import ai.hyacinth.core.service.examples.order.web.OrderController;
import ai.hyacinth.core.service.jpa.config.JpaConfig;
import ai.hyacinth.core.service.examples.user.UserApiConfig;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackageClasses = {Order.class}) // domain-class
@EnableJpaRepositories(basePackageClasses = {OrderRepo.class}) // repo
@ComponentScan(
    basePackageClasses = {OrderService.class, OrderController.class}) // service and controller
@Import({EndpointConfig.class, JpaConfig.class, DiscoveryConfig.class, UserApiConfig.class}) // support modules
public class OrderApplicationConfig {}
