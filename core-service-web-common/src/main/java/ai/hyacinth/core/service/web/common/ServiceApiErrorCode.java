package ai.hyacinth.core.service.web.common;

public interface ServiceApiErrorCode {
  String getCode();

  default String getMessage() {
    return toString();
  }

  default int getHttpStatusCode() {
    return 400; // BAD REQUEST
  }
}
