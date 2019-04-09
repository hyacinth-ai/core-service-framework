package ai.hyacinth.core.service.gateway.server.configprops;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GatewaySecurityProperties {
  /**
   * this switch determines different behavior when the authority of a rule is not specified
   */
  private boolean authenticatedRequired = true;

  private String logoutUrl = "/logout";
  private String logoutPayload = "{\"status\":\"success\"}";
}
