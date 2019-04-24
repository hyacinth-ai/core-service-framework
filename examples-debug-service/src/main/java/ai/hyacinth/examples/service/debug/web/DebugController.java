package ai.hyacinth.examples.service.debug.web;

import ai.hyacinth.core.service.bus.support.service.BusService;
import ai.hyacinth.core.service.web.common.ServiceApiConstants;
import ai.hyacinth.examples.service.debug.dto.ApiCall;
import ai.hyacinth.examples.service.debug.service.DebugService;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(ServiceApiConstants.API_PREFIX)
public class DebugController {
  @Autowired private DebugService debugService;
  @Autowired private BusService busService;

  @GetMapping("/history")
  public List<ApiCall> history() {
    return debugService.findCallHistory();
  }

  @GetMapping("/echo")
  public Map<?, ?> echo(HttpServletRequest request) {
    return request.getParameterMap();
  }

  @PostMapping("/echo")
  public Map<?, ?> echo(
      @RequestBody(required = false) Map<?, ?> requestBody) {
    return requestBody;
  }

  @PostMapping("/event")
  public String event(@RequestBody(required = false) Map<?, ?> eventPayload) {
    busService.publish(BusService.ALL_SERVICES, "debug", eventPayload);
    busService.publish(BusService.ALL_SERVICES, "test", new TestBean("object"));
    return "ok";
  }

  @RequestMapping(value = {"/call", "/call/**", "/**"})
  public ApiCall debug(
      @RequestBody(required = false) Object requestBody,
      HttpServletRequest request,
      @RequestHeader Map<?, ?> httpHeaders) {
    ApiCall apiCall = new ApiCall();
    apiCall.setRequestMethod(request.getMethod());
    apiCall.setPath(request.getRequestURI());
    apiCall.setRequestParameters(request.getParameterMap());
    apiCall.setRequestHeaders(httpHeaders);
    apiCall.setRequestTime(new Date());
    apiCall.setRequestBody(requestBody);
    log.info("Api call received: {}", apiCall);
    debugService.recordCallHistory(apiCall);
    return apiCall;
  }
}
