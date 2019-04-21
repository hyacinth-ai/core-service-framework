package ai.hyacinth.core.service.gateway.server.configprops;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("ai.hyacinth.core.service.gateway.server")
@Data
@NoArgsConstructor
public class GatewayServerProperties {
  private GatewaySecurityProperties security = new GatewaySecurityProperties();
  private GatewayJwtProperties jwt = new GatewayJwtProperties();
  private List<GatewayRuleProperties> rules = new ArrayList<>();
  private GatewayRateLimiterProperties rateLimiter = new GatewayRateLimiterProperties();
}
