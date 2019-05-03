package ai.hyacinth.core.service.trigger.server.config;

import ai.hyacinth.core.service.discovery.support.config.DiscoveryConfig;
import ai.hyacinth.core.service.endpoint.support.config.EndpointConfig;
import ai.hyacinth.core.service.jpa.config.JpaConfig;
import ai.hyacinth.core.service.trigger.server.domain.ServiceTrigger;
import ai.hyacinth.core.service.trigger.server.repo.ServiceTriggerRepository;
import ai.hyacinth.core.service.trigger.server.service.TriggerService;
import ai.hyacinth.core.service.trigger.server.service.impl.SpringBeanJobFactory;
import ai.hyacinth.core.service.trigger.server.web.TriggerController;
import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Import({JpaConfig.class, EndpointConfig.class, DiscoveryConfig.class})
@ComponentScan(basePackageClasses = {TriggerService.class, TriggerController.class})
@EnableJpaRepositories(basePackageClasses = {ServiceTriggerRepository.class})
@EntityScan(basePackageClasses = ServiceTrigger.class)
@EnableCaching
public class TriggerApplicationConfig {
  private Properties quartzProperties() {
    Properties prop = new Properties();
    prop.put("org.quartz.scheduler.instanceId", "AUTO");
    prop.put("org.quartz.scheduler.jmx.export", "true");
    prop.put(
        "org.quartz.jobStore.class",
        "org.quartz.impl.jdbcjobstore.JobStoreTX"); // org.quartz.simpl.RAMJobStore
    prop.put("org.quartz.jobStore.isClustered", "true");
    return prop;
  }

  @Bean
  public SchedulerFactoryBean schedulerFactoryBean(
      DataSource dataSource, SpringBeanJobFactory jobFactory) {
    SchedulerFactoryBean factory = new SchedulerFactoryBean();
    factory.setQuartzProperties(quartzProperties());
    factory.setOverwriteExistingJobs(false);
    factory.setDataSource(dataSource);
    factory.setJobFactory(jobFactory);
    factory.setAutoStartup(true);
    return factory;
  }

  @Bean
  @LoadBalanced
  @ConditionalOnMissingBean(WebClient.Builder.class)
  public WebClient.Builder loadBalancedWebClientBuilder() {
    return WebClient.builder();
  }
}
