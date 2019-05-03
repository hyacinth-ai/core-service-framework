package ai.hyacinth.core.service.trigger.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TriggerQueryRequest {
  private Long id;
  private String name;
  private String service;
  private Boolean enabled;
}
