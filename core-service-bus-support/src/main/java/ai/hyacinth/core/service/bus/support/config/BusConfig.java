package ai.hyacinth.core.service.bus.support.config;

import ai.hyacinth.core.service.bus.support.event.BusEvent;
import ai.hyacinth.core.service.bus.support.service.BusService;
import org.springframework.cloud.bus.jackson.RemoteApplicationEventScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@RemoteApplicationEventScan(basePackageClasses = BusEvent.class)
@ComponentScan(basePackageClasses = BusService.class)
public class BusConfig {}
