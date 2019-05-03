package ai.hyacinth.core.service.trigger.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TriggerChangeResult {
  private Long id;
  private boolean completed;
}
