package ai.hyacinth.service.user.service.impl;

import ai.hyacinth.core.service.web.common.ServiceApiErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserServiceErrorCode implements ServiceApiErrorCode {
  USER_EXISTS(HttpStatus.CONFLICT, "100000"),
  EMPTY_PASSWORD(HttpStatus.BAD_REQUEST, "100001"),
  USER_PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "100002"),
  NO_SUCH_USER(HttpStatus.NOT_FOUND, "100003");

  private HttpStatus httpStatus;
  private String code;

  @Override
  public int getHttpStatusCode() {
    return httpStatus.value();
  }
}
