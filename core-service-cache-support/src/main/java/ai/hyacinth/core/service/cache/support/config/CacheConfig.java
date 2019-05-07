package ai.hyacinth.core.service.cache.support.config;

import ai.hyacinth.core.service.cache.support.service.CacheProviderPrinter;
import ai.hyacinth.core.service.module.factory.YamlPropertySourceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.cache.RedisCacheManager;

@Configuration
@EnableCaching
@Slf4j
@ComponentScan(basePackageClasses = {CacheProviderPrinter.class})
public class CacheConfig {
}
