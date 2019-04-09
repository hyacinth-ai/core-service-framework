package ai.hyacinth.core.service.web.support.errorhandler;

import ai.hyacinth.core.service.web.common.ServiceApiErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ServiceApiCommonErrorCode implements ServiceApiErrorCode {
  BAD_REQUEST(HttpStatus.BAD_REQUEST, "E90400"),
  METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "E90405"),
  FORBIDDEN(HttpStatus.FORBIDDEN, "E90403"),

  UNKNOWN_ERROR(HttpStatus.BAD_REQUEST, "E80000"),
  REQUEST_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "E80100"),
  NETWORK_ERROR(HttpStatus.BAD_GATEWAY, "E80200")
  ;

  private HttpStatus httpStatus;
  private String code;
}
