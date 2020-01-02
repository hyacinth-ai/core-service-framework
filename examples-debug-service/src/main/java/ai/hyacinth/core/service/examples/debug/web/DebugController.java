package ai.hyacinth.core.service.examples.debug.web;

import ai.hyacinth.core.service.examples.debug.dto.ApiCall;
import ai.hyacinth.core.service.examples.debug.service.DebugService;
import ai.hyacinth.core.service.web.common.ServiceApiConstants;
import ai.hyacinth.core.service.web.common.ServiceApiException;
import ai.hyacinth.core.service.web.common.error.CommonServiceErrorCode;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Slf4j
@RestController
@RequestMapping(ServiceApiConstants.API_PREFIX)
public class DebugController {
  @Autowired private DebugService debugService;
  //  @Autowired private BusService busService;

  @GetMapping("/bench")
  public String bench() {
    return "1";
  }

  @GetMapping("/ping")
  public String ping() {
    return "pong";
  }

  @GetMapping("/echo")
  public Map<?, ?> echo(HttpServletRequest request) {
    return request.getParameterMap();
  }

  @PostMapping("/echo")
  public Map<?, ?> echo(@RequestBody(required = false) Map<?, ?> requestBody) {
    return requestBody;
  }

  @GetMapping({"/addr", "/address"})
  public Map<?, ?> address() {
    Map<String, List<String>> addressList = new HashMap<>();
    try {
      Enumeration<NetworkInterface> eni = NetworkInterface.getNetworkInterfaces();
      while (eni.hasMoreElements()) {
        NetworkInterface ni = eni.nextElement();
        //        if (ni.isLoopback()) {
        //          continue;
        //        }
        addressList.put(
            ni.getDisplayName(),
            ni.getInterfaceAddresses().stream()
                //                .filter(e -> !e.getAddress().isLoopbackAddress())
                .map(InterfaceAddress::toString)
                .collect(Collectors.toList()));
      }
    } catch (SocketException e) {
    }

    Map<String, String> host = new HashMap<>();
    try {
      InetAddress ia = null;
      ia = InetAddress.getLocalHost();
      String hostName = ia.getHostName();
      String hostAddr = ia.getHostAddress();
      host.put("hostname", hostName);
      host.put("address", hostAddr);
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }

    HashMap<String, Object> result = new HashMap<>();
    result.put("addressList", addressList);
    result.put("localhost", host);
    return result;
  }

  @PostMapping("/event")
  public String event(@RequestBody(required = false) Map<?, ?> eventPayload) {
    //    busService.publish(BusService.ALL_SERVICES, "debug", eventPayload);
    //    busService.publish(BusService.ALL_SERVICES, "test", new TestBean("object"));
    return "sent";
  }

  @RequestMapping("/exception")
  public String exception() {
    log.debug("expected to throw a exception.");
    throw new ServiceApiException(
        CommonServiceErrorCode.NETWORK_ERROR, new UnsupportedOperationException());
  }

  @RequestMapping(value = {"/call", "/call/**", "/**"})
  public ApiCall call(
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
    log.debug("Api call received: {}", apiCall);
    debugService.recordCallHistory(apiCall);
    return apiCall;
  }

  @GetMapping("/history")
  public List<ApiCall> history() {
    return debugService.findCallHistory();
  }

  //  @Autowired
  //  private ConversionService conversionService;

  @GetMapping("/data")
  public StreamingResponseBody data(@RequestParam String size) {
    final DataSize result = DataSize.parse(size);
    return (output) -> {
      byte[] content = ByteBuffer.allocate((int) result.toBytes()).array();
      output.write(content);
    };
  }
}
