package ai.hyacinth.core.service.gateway.server.config;

import ai.hyacinth.core.service.discovery.support.config.DiscoveryConfig;
import ai.hyacinth.core.service.gateway.server.configprops.GatewayServerProperties;
import ai.hyacinth.core.service.gateway.server.configprops.ResponsePostProcessingType;
import ai.hyacinth.core.service.gateway.server.route.RouteConfig;
import ai.hyacinth.core.service.gateway.server.route.SecurityConfig;
import ai.hyacinth.core.service.gateway.server.web.GatewayController;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Configuration
@Import({DiscoveryConfig.class, RouteConfig.class, SecurityConfig.class})
@ComponentScan(basePackageClasses = {GatewayConfig.class, GatewayController.class})
@EnableConfigurationProperties({GatewayServerProperties.class})
public class GatewayConfig {
}
