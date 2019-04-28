package ai.hyacinth.core.service.endpoint.support.errorhandler;

import ai.hyacinth.core.service.endpoint.support.error.ServiceApiCommonErrorCode;
import ai.hyacinth.core.service.web.common.ServiceApiConstants;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class ServiceApiErrorAttributes implements ErrorAttributes {

  private ErrorAttributes delegate = new DefaultErrorAttributes();

  @Value("${spring.application.name}")
  private String applicationName;

  @Override
  public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
    Map<String, Object> attrs = delegate.getErrorAttributes(webRequest, includeStackTrace);

//    ServletWebRequest servletWebRequest = (ServletWebRequest) webRequest;
//    servletWebRequest.getNativeRequest(HttpServletRequest.class);
    String path = (String) attrs.get("path");
    // rewrite response (including #404)
    if (path != null && path.startsWith(ServiceApiConstants.API_PREFIX + "/")) {
      Map<String, Object> originalError = new HashMap<>(attrs);
      Optional<HttpStatus> httpStatus = getHttpStatus(attrs);
      if (httpStatus.isPresent()) {
        int httpStatusCode = httpStatus.get().value();
        attrs.put("code", "E90" + httpStatusCode);
        attrs.put("message", httpStatus.get().name());
      } else {
        attrs.put("code", ServiceApiCommonErrorCode.UNKNOWN_ERROR.getCode());
        attrs.put("message", ServiceApiCommonErrorCode.UNKNOWN_ERROR.getMessage());
      }
      attrs.put("data", Collections.singletonMap("originalErrorAttributes", originalError));
      attrs.put("status", "error");
      attrs.put("service", applicationName);
      attrs.remove("error");
    }
    return attrs;
  }

  private Optional<HttpStatus> getHttpStatus(Map<String, Object> attrs) {
    try {
      return Optional.of(HttpStatus.valueOf((Integer)attrs.get("status")));
    } catch (Exception ex) {
      return Optional.empty();
    }
  }

  @Override
  public Throwable getError(WebRequest webRequest) {
    return delegate.getError(webRequest);
  }
}
