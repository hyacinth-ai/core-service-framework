package ai.hyacinth.examples.service.debug.dto;

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
  Object requestBody;
  Object requestHeaders;
  Date requestTime;
}
