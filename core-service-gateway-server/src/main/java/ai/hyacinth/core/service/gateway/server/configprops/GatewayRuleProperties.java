package ai.hyacinth.core.service.gateway.server.configprops;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
  /** predicate */
  @NotNull private String path;

  @Nullable private HttpMethod method; // empty means no restriction
  private List<String> authority = new ArrayList<>();

  /** route */
  private String service;

  private String uri;

  /** request rewrite */
  private Map<String, String> requestParam = new LinkedHashMap<>();

  private Map<String, Object> requestBody = new LinkedHashMap<>();
  private String requestBodyJson;

  /** security-related api hint */
  private boolean authenticationApi;

  /** response rewrite with http status reset */
  private List<ResponsePostProcessingType> postProcessing = new LinkedList<>();

  /** no effect on current implementation. secure headers is achieved by spring-security instead of gateway filter */
  private boolean secureHttpHeaders = true;
}
