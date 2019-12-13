package ai.hyacinth.core.service.api.support.config;

import ai.hyacinth.core.service.web.common.ServiceApiErrorResponse;
import ai.hyacinth.core.service.web.common.ServiceApiException;
import ai.hyacinth.core.service.web.common.error.CommonServiceErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Slf4j
public class ServiceApiErrorDecoder implements ErrorDecoder {
  @Autowired private ObjectMapper objectMapper;

  @Override
  public Exception decode(String methodKey, Response response) {
    if (isJsonType(response)) {
      try {
        ServiceApiErrorResponse errorResponse =
            objectMapper.readValue(response.body().asInputStream(), ServiceApiErrorResponse.class);
        return new ServiceApiException(response.status(), errorResponse);
      } catch (Exception parseError) {
        log.warn(
            "API calling error occurs but error payload is incompatible to parse.", parseError);
        return new ServiceApiException(CommonServiceErrorCode.UNSUPPORTED_MEDIA_TYPE, parseError);
      }
    } else {
      String text;
      try {
        text = IOUtils.toString(response.body().asInputStream(), StandardCharsets.UTF_8.name());
      } catch (Exception encodingError) {
        text = "Unknown error";
      }
      return new ServiceApiException(CommonServiceErrorCode.INTERNAL_ERROR, text);
    }
  }

  private static boolean isJsonType(Response response) {
    Collection<String> typeValues =
        response.headers().getOrDefault(HttpHeaders.CONTENT_TYPE, Collections.emptyList());
    if (typeValues.size() > 0) {
      String contentType = typeValues.iterator().next();
      if (contentType.startsWith(MediaType.APPLICATION_JSON_VALUE)) {
        return true;
      }
    }
    return false;
  }
}
