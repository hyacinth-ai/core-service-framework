package ai.hyacinth.core.service.cache.support.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CacheProviderPrinter
    implements ApplicationListener<ApplicationReadyEvent>, ApplicationContextAware {
  private ApplicationContext ctx;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.ctx = applicationContext;
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    ConfigurableApplicationContext ctx = event.getApplicationContext();
    if (ctx.equals(this.ctx)) {
      log.info(
          "Cache is provided by {}, cache-names: {}", ctx.getEnvironment().getProperty("spring.cache.type"), ctx.getEnvironment().getProperty("spring.cache.cache-names"));
    }
  }
}
