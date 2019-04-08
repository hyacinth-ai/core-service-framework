package ai.hyacinth.core.service.cache.support.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {
  @Bean
  public CacheManagerCustomizer<RedisCacheManager> cacheManagerCustomizer() {
    return cacheManager -> {
      //
    };
  }
}
