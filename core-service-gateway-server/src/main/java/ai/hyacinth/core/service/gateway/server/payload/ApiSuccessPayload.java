package ai.hyacinth.core.service.gateway.server.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiSuccessPayload {
  private String status = "success";

  @JsonRawValue
  @JsonInclude(Include.NON_NULL)
  protected String data;
}
