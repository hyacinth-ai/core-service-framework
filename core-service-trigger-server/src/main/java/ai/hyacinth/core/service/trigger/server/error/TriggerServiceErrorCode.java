package ai.hyacinth.core.service.trigger.server.error;

import ai.hyacinth.core.service.web.common.ServiceApiErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TriggerServiceErrorCode implements ServiceApiErrorCode {
  TRIGGER_EXISTS(HttpStatus.CONFLICT, "100000"),
  WRONG_CRON_EXPRESSION(HttpStatus.BAD_REQUEST, "100001"),
  TIMEOUT_REQUIRED(HttpStatus.BAD_REQUEST, "100002"),
  TRIGGER_NOT_FOUND(HttpStatus.NOT_FOUND, "100003"),
  QUARTZ_ERROR(HttpStatus.BAD_REQUEST, "100004");

  private HttpStatus httpStatus;
  private String code;

  @Override
  public int getHttpStatusCode() {
    return httpStatus.value();
  }
}
