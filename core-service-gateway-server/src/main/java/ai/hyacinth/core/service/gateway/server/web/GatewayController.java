package ai.hyacinth.core.service.gateway.server.web;

import ai.hyacinth.core.service.web.common.ServiceApiConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping(ServiceApiConstants.API_PREFIX)
@Controller
public class GatewayController {
  @GetMapping("/ping")
  @ResponseBody
  public String ping() {
    return "pong";
  }
}
