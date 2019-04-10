package ai.hyacinth.core.service.gateway.server.configprops;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;

@ConfigurationProperties("ai.hyacinth.core.service.gateway.server")
@Data
@NoArgsConstructor
public class GatewayServerProperties {
  private GatewaySecurityProperties security = new GatewaySecurityProperties();
  private List<GatewayRuleProperties> rules = new ArrayList<>();
}
