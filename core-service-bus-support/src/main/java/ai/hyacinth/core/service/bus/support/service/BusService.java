package ai.hyacinth.core.service.bus.support.service;

import ai.hyacinth.core.service.bus.support.event.BusEvent;

public interface BusService {

  String ALL_SERVICES = "**";

  <T> void publish(String targetService, String eventType, T payload);

  void publish(BusEvent<?> event);

}
