package ai.hyacinth.core.service.web.common;

public class ServiceApiException extends RuntimeException {

  private int httpStatusCode;

  private ServiceApiErrorResponse errorResponse;

  public ServiceApiException() {
    super();
  }

  public ServiceApiException(
      int httpStatus, ServiceApiErrorResponse errorResponse, Throwable throwable) {
    super(errorResponse.getMessage(), throwable);
    this.httpStatusCode = httpStatus;
    this.errorResponse = errorResponse;
  }

  public ServiceApiException(int httpStatus, ServiceApiErrorResponse errorResponse) {
    this(httpStatus, errorResponse, null);
  }

  public ServiceApiException(ServiceApiErrorCode errorCode, Throwable throwable) {
    this(errorCode.getHttpStatusCode(), createErrorResponse(errorCode, null), throwable);
  }

  public ServiceApiException(ServiceApiErrorCode errorCode, Object data) {
    this(errorCode.getHttpStatusCode(), createErrorResponse(errorCode, data), null);
  }

  public ServiceApiException(ServiceApiErrorCode errorCode) {
    this(errorCode, null);
  }

  private static ServiceApiErrorResponse createErrorResponse(
      ServiceApiErrorCode errorCode, Object data) {
    ServiceApiErrorResponse response = new ServiceApiErrorResponse();
    response.setCode(errorCode.getCode());
    response.setMessage(errorCode.getMessage());
    response.setData(data);
    return response;
  }

  public int getHttpStatusCode() {
    return httpStatusCode;
  }

  public void setHttpStatusCode(int httpStatusCode) {
    this.httpStatusCode = httpStatusCode;
  }

  public ServiceApiErrorResponse getErrorResponse() {
    return errorResponse;
  }
}
