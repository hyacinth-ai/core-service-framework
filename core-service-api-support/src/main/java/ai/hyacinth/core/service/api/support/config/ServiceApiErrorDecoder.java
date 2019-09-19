package ai.hyacinth.core.service.api.support.config;

import ai.hyacinth.core.service.web.common.ServiceApiErrorResponse;
import ai.hyacinth.core.service.web.common.ServiceApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ServiceApiErrorDecoder implements ErrorDecoder {

  private static Logger logger = LoggerFactory.getLogger(ServiceApiErrorDecoder.class);

  @Autowired private ObjectMapper objectMapper;

  @Override
  public Exception decode(String methodKey, Response response) {
    try {
      ServiceApiErrorResponse errorResponse =
          objectMapper.readValue(response.body().asInputStream(), ServiceApiErrorResponse.class);
      return new ServiceApiException(response.status(), errorResponse);
    } catch (Exception parseError) {
      logger.warn("error payload parsing error. maybe incompatible error response.", parseError);
      return new ServiceApiException();
    }
  }
}
