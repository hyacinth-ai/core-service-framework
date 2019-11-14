package ai.hyacinth.core.service.web.common.error;

import ai.hyacinth.core.service.web.common.ServiceApiErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonServiceErrorCode implements ServiceApiErrorCode {

  BAD_REQUEST(HttpStatus.BAD_REQUEST, "E90400"),
  FORBIDDEN(HttpStatus.FORBIDDEN, "E90403"),
  NOT_FOUND(HttpStatus.NOT_FOUND, "E90404"),
  METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "E90405"),
  CONFLICT(HttpStatus.CONFLICT, "E90409"),
  PRECONDITION_FAILED(HttpStatus.PRECONDITION_FAILED, "E90412"),
  UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "E90415"),

  UNKNOWN_ERROR(HttpStatus.BAD_REQUEST, "E80000"),
  REQUEST_ERROR(HttpStatus.BAD_REQUEST, "E80100"), // incorrect request parameter or payload
  NETWORK_ERROR(HttpStatus.BAD_REQUEST, "E80200"),
  INTERNAL_ERROR(HttpStatus.BAD_REQUEST, "E80300"),
  ;

  private HttpStatus httpStatus;
  private String code;

  @Override
  public int getHttpStatusCode() {
    return httpStatus.value();
  }
}
