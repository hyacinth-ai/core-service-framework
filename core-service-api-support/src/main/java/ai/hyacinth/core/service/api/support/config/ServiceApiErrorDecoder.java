package ai.hyacinth.core.service.api.support.config;

import ai.hyacinth.core.service.web.common.ServiceApiErrorResponse;
import ai.hyacinth.core.service.web.common.ServiceApiException;
import ai.hyacinth.core.service.web.common.error.CommonServiceErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ServiceApiErrorDecoder implements ErrorDecoder {
  @Autowired private ObjectMapper objectMapper;

  @Override
  public Exception decode(String methodKey, Response response) {
    try {
      ServiceApiErrorResponse errorResponse =
          objectMapper.readValue(response.body().asInputStream(), ServiceApiErrorResponse.class);
      return new ServiceApiException(response.status(), errorResponse);
    } catch (Exception parseError) {
      log.warn("API calling error occurs but error payload is incompatible to parse.", parseError);
      return new ServiceApiException(CommonServiceErrorCode.UNSUPPORTED_MEDIA_TYPE, parseError);
    }
  }
}
