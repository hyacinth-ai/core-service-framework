package ai.hyacinth.core.service.trigger.server.web;

import ai.hyacinth.core.service.trigger.server.dto.TriggerChangeResult;
import ai.hyacinth.core.service.trigger.server.dto.TriggerCreationRequest;
import ai.hyacinth.core.service.trigger.server.dto.TriggerInfo;
import ai.hyacinth.core.service.trigger.server.dto.TriggerQueryRequest;
import ai.hyacinth.core.service.trigger.server.dto.TriggerUpdateRequest;
import ai.hyacinth.core.service.trigger.server.service.TriggerService;
import ai.hyacinth.core.service.web.common.ServiceApiConstants;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(ServiceApiConstants.API_PREFIX)
public class TriggerController {
  @Autowired private TriggerService triggerService;

  @PostMapping(value = "/triggers")
  public TriggerInfo createTrigger(
      @Validated @RequestBody TriggerCreationRequest registrationRequest) {
    return triggerService.createTrigger(registrationRequest);
  }

  @GetMapping(value = "/triggers")
  public List<TriggerInfo> findTriggers(TriggerQueryRequest queryRequest) {
    return triggerService.findAllTriggers(queryRequest);
  }

  @RequestMapping(
      method = {RequestMethod.GET},
      value = "/triggers/{triggerId}")
  public TriggerInfo findTrigger(@PathVariable Long triggerId) {
    return triggerService.findTriggerById(triggerId);
  }

  @RequestMapping(
      method = {RequestMethod.DELETE},
      value = "/triggers/{triggerId}")
  public TriggerChangeResult removeTrigger(@PathVariable Long triggerId) {
    return triggerService.removeTrigger(triggerId);
  }

  @RequestMapping(
      method = {RequestMethod.PATCH},
      value = "/triggers/{triggerId}")
  public TriggerInfo updateTrigger(
      @PathVariable Long triggerId, @Validated @RequestBody TriggerUpdateRequest updateRequest) {
    return triggerService.updateTrigger(triggerId, updateRequest);
  }
}
