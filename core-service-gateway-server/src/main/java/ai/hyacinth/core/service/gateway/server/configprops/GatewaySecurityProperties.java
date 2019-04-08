package ai.hyacinth.core.service.gateway.server.configprops;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GatewaySecurityProperties {
  /**
   * this switch causes different behavior when the role of a router rule is empty
   */
  private boolean authenticatedRequired = true;

  private String logoutUrl = "/logout";
  private String logoutPayload = "{\"status\":\"success\"}";
}
