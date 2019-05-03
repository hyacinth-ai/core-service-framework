package ai.hyacinth.core.service.trigger.server.dto;

import java.time.Duration;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TriggerUpdateRequest {
  private String cron;

  private Duration timeout;

  private Boolean enabled;
}
