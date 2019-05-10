package ai.hyacinth.core.service.web.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class ServiceApiErrorResponse extends ServiceApiResponse {

  protected String code;
  protected String message;

  // the following fields are filled by exception handler
  private Date timestamp;
  @JsonInclude(Include.NON_NULL)
  private String path;
  @JsonInclude(Include.NON_NULL)
  private String service;

  public ServiceApiErrorResponse() {
    this.status = "error";
  }
}
