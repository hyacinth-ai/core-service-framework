package ai.hyacinth.core.service.gateway.server.configprops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;

@Data
@NoArgsConstructor
public class GatewayRuleProperties {
  /**
   * predicate
   */
  @NotNull
  private String path;
  @Nullable
  private HttpMethod method; // empty means no restriction
  private List<String> role = new ArrayList<>();

  /**
   * route
   */
  private String service;
  private String uri;

  /**
   * request rewrite
   */
  private Map<String, String> requestParameters = new HashMap<>();
  private Map<String, String> requestBody = new HashMap<>();

  /**
   * response rewrite
   */
  private ResponsePostProcessingType postProcessing = ResponsePostProcessingType.NONE;

  /**
   * security api hint
   */
  private boolean authenticationApi;

  /**
   * secure headers is always implemented by spring-security instead of gateway
   */
  private boolean secureHttpHeaders = true;
}
