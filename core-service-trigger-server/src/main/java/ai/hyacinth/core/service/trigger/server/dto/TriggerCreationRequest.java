package ai.hyacinth.core.service.trigger.server.dto;

import ai.hyacinth.core.service.trigger.server.dto.type.ServiceTriggerMethodType;
import io.swagger.annotations.ApiModelProperty;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpMethod;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TriggerCreationRequest {
  @NotNull private String name;
  private String service;

  private ServiceTriggerMethodType triggerMethod;

  @NotNull @Builder.Default private HttpMethod httpMethod = HttpMethod.GET;
  private String url;

  private Map<String, Object> params;

  @ApiModelProperty("Quartz cron expression. Try '0 0/2 * ? * * *' for every 2 minutes.")
  @NotNull private String cron;

  @ApiModelProperty("Not used now.")
  private Duration timeout;

  @Builder.Default @NotNull private Boolean enabled = Boolean.TRUE;
}
