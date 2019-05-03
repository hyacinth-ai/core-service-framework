package ai.hyacinth.core.service.trigger.server.dto;

import ai.hyacinth.core.service.trigger.server.dto.type.ServiceTriggerMethodType;
import java.time.Duration;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriggerInfo {
  private Long id;

  private String service;
  private String name;

  private ServiceTriggerMethodType triggerMethod;

  private HttpMethod httpMethod;
  private String url;

  private String cron;
  private Duration timeout;

  private boolean enabled;

  private Map<String, Object> params;
}
