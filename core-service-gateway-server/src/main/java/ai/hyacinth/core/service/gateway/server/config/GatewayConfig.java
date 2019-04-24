package ai.hyacinth.core.service.gateway.server.config;

import ai.hyacinth.core.service.bus.support.config.BusConfig;
import ai.hyacinth.core.service.discovery.support.config.DiscoveryConfig;
import ai.hyacinth.core.service.gateway.server.configprops.GatewayServerProperties;
import ai.hyacinth.core.service.gateway.server.jwt.JwtService;
import ai.hyacinth.core.service.gateway.server.ratelimiter.SimpleRateLimiter;
import ai.hyacinth.core.service.gateway.server.web.GatewayController;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({DiscoveryConfig.class, RouteConfig.class, SecurityConfig.class, BusConfig.class})
@ComponentScan(
    basePackageClasses = {
      GatewayConfig.class,
      GatewayController.class,
      JwtService.class,
      SimpleRateLimiter.class
    })
@EnableConfigurationProperties({GatewayServerProperties.class})
public class GatewayConfig {}
