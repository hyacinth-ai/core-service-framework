package ai.hyacinth.core.service.examples.debug.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Date;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ApiCall {
  String path;
  String requestMethod;
  Map<String, ?> requestParameters;

  @JsonInclude(Include.NON_NULL)
  Object requestBody;

  Object requestHeaders;
  Date requestTime;
}
