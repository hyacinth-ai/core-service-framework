package ai.hyacinth.core.service.cache.support.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CacheProviderLogger
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
      String cacheTypeProp = ctx.getEnvironment().getProperty("spring.cache.type");
      log.info("Cache type is currently set to: {}", cacheTypeProp);
      String[] cmNames = ctx.getBeanNamesForType(CacheManager.class);
      for (String name : cmNames) {
        log.info("cacheManager {} loaded.", name);
      }
      log.info("cache-names: {}", ctx.getEnvironment().getProperty("spring.cache.cache-names"));
    }
  }
}
