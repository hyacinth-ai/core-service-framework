package ai.hyacinth.core.service.gateway.server.configprops;

import java.time.Duration;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@ConfigurationProperties
public class GatewayRateLimiterProperties {
  private Long replenishRate; // disabled if rate == null
  private Duration replenishPeriod = Duration.ofMinutes(1);
}
