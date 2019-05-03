package ai.hyacinth.core.service.trigger.server.service.impl;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.stereotype.Component;

@Component
public class SpringBeanJobFactory extends AdaptableJobFactory {

  @Autowired private ApplicationContext ctx;

  @Override
  protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
    Object jobInstance = super.createJobInstance(bundle);
    ctx.getAutowireCapableBeanFactory().autowireBean(jobInstance);
    return jobInstance;
  }
}
