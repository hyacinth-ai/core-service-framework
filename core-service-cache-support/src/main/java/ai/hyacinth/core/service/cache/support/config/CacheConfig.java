package ai.hyacinth.core.service.cache.support.config;

import ai.hyacinth.core.service.cache.support.service.CacheProviderLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@Slf4j
@ComponentScan(basePackageClasses = {CacheProviderLogger.class})
public class CacheConfig {
}
