package ai.hyacinth.core.service.web.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface ServiceApiErrorCode {
  String getCode();

  default String getMessage() {
    return toString();
  }

  int getHttpStatusCode();

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class SimpleServiceApiErrorCodeImpl implements ServiceApiErrorCode {
    protected String code;
    protected String message;
    protected int httpStatusCode;
  }

  static ServiceApiErrorCode of(String code, String message, int httpStatusCode) {
    return new SimpleServiceApiErrorCodeImpl(code, message, httpStatusCode);
  }
}
