package ai.hyacinth.core.service.web.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ServiceApiResponse {
  protected String status = "success";

  @JsonInclude(JsonInclude.Include.NON_NULL)
  protected Object data;
}
