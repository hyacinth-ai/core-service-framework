package ai.hyacinth.examples.service.order.service.impl;

import ai.hyacinth.core.service.web.common.ServiceApiErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum OrderServiceErrorCode implements ServiceApiErrorCode {
  PUBLIC_USER_ONLY(HttpStatus.BAD_REQUEST, "100000"),
  NO_SUCH_USER(HttpStatus.BAD_REQUEST, "100003");

  private HttpStatus httpStatus;
  private String code;

  @Override
  public int getHttpStatusCode() {
    return httpStatus.value();
  }
}
