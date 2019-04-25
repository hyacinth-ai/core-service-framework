package ai.hyacinth.core.service.bus.support.service.impl;

import ai.hyacinth.core.service.bus.support.event.BusEvent;
import ai.hyacinth.core.service.bus.support.service.BusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.bus.BusProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BusServiceImpl implements BusService {
  @Autowired private ApplicationContext applicationContext;

  @Autowired private BusProperties busProperties;

  @Override
  public <T> void publish(String targetService, String eventType, T payload) {
    publish(createEvent(targetService, eventType, payload));
  }

  private <T> BusEvent createEvent(String targetService, String eventType, T payload) {
    BusEvent<T> event = new BusEvent<>(this, busProperties.getId(), targetService);
    event.setEventType(eventType);
    event.setPayload(payload);
    event.setPayloadType(getPayloadType(payload));
    return event;
  }

  private <T> String getPayloadType(T payload) {
    return payload != null ? payload.getClass().getName() : null;
  }

  @Override
  public void publish(BusEvent<?> event) {
    applicationContext.publishEvent(event);
  }

  @EventListener
  public void logBusEvent(BusEvent<?> busEvent) {
    // busEvent.getEventType();
    log.info(
        "Bus event received: {}, sent-by-me: {}",
        busEvent,
        busEvent.getOriginService().equals(busProperties.getId()));
  }
}
