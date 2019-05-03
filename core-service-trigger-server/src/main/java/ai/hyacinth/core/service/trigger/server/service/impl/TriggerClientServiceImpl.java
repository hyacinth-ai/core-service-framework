package ai.hyacinth.core.service.trigger.server.service.impl;

import ai.hyacinth.core.service.trigger.server.dto.TriggerInfo;
import ai.hyacinth.core.service.trigger.server.dto.type.ServiceTriggerMethodType;
import ai.hyacinth.core.service.trigger.server.error.TriggerServiceErrorCode;
import ai.hyacinth.core.service.trigger.server.service.TriggerService;
import ai.hyacinth.core.service.web.common.ServiceApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class TriggerClientServiceImpl implements TriggerClientService {
  @Autowired private TriggerService triggerService;
  @Autowired private ApplicationContext applicationContext;
  @Autowired private WebClient.Builder webClientBuilder;

  //  public interface TriggerClient {
  //    @RequestMapping(
  //        path = {"{path}"},
  //        method = {RequestMethod.POST},
  //        consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
  //    String triggerCall(@PathVariable("path") String path);
  //  }
  //  @PostConstruct
  //  public void setupFeignClientBuilder() {
  //    FeignClientBuilder feignClientBuilder = new FeignClientBuilder(applicationContext);
  //  }

  @Override
  public void trigger(Long triggerId) {
    log.info("start trigger, ID: {}", triggerId);
    TriggerInfo sti = triggerService.findTriggerById(triggerId);
    if (sti == null) {
      log.error("cannot find trigger, deleted? ID: {}", triggerId);
      throw new ServiceApiException(TriggerServiceErrorCode.TRIGGER_NOT_FOUND);
    } else {
      log.info("start trigger, detail: {}", sti);
    }

    if (sti.isEnabled()) {
      switch (sti.getTriggerMethod()) {
        case LOG:
          log.info("trigger logged");
          break;
        case SERVICE_URL:
        case URL:
          try {
            String response = httpRequest(sti);
            log.info("trigger response: {}", response);
          } catch (Exception httpException) {
            log.error("trigger url error", httpException);
          }
          break;
        default:
          throw new UnsupportedOperationException(
              "Cannot support trigger method:" + sti.getTriggerMethod());
      }
    } else {
      log.info("trigger disabled, no action. ID: {} ", triggerId);
    }
  }

  protected String httpRequest(TriggerInfo sti) {
    WebClient webClient;
    if (sti.getTriggerMethod().equals(ServiceTriggerMethodType.SERVICE_URL)) {
      webClient =
          webClientBuilder
              .baseUrl("http://" + sti.getService())
              .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .build();
    } else {
      webClient =
          WebClient.builder()
              .baseUrl(sti.getUrl())
              .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .build();
    }

    return webClient
        .method(sti.getHttpMethod())
        .uri(sti.getUrl())
        .body(
            sti.getParams() == null
                ? BodyInserters.empty()
                : BodyInserters.fromObject(sti.getParams()))
        .retrieve()
        .bodyToMono(String.class)
        .block();
  }
}
