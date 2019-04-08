package ai.hyacinth.core.service.gateway.server.config;

import ai.hyacinth.core.service.discovery.support.config.DiscoveryConfig;
import ai.hyacinth.core.service.gateway.server.configprops.GatewayServerProperties;
import ai.hyacinth.core.service.gateway.server.route.RouteConfig;
import ai.hyacinth.core.service.gateway.server.route.SecurityConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({DiscoveryConfig.class, RouteConfig.class, SecurityConfig.class})
@EnableConfigurationProperties({GatewayServerProperties.class})
public class GatewayConfig {
}
