package ai.hyacinth.core.service.gateway.server.ratelimiter;

import ai.hyacinth.core.service.gateway.server.ratelimiter.SimpleRateLimiter.SimpleRateLimiterConfig;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.security.util.FieldUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * current implementation points:
 *
 * <p>1. use localtime instead of redis server time to reduce 1 time() call
 *
 * <p>2. if api-call count could not be retrieved correctly due to redis connection, the result
 * is "allow".
 *
 * <p>3. anonymous accessible API always pass the rate limiter check.
 *
 * <p>4. in future version, for anonymous access, IP could be a key to restrict access.
 */
@Slf4j
@Data
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Primary
public class SimpleRateLimiter implements RateLimiter<SimpleRateLimiterConfig> {
  public SimpleRateLimiter() {}

  @Autowired private LettuceConnectionFactory lettuceConnectionFactory;

  private Long replenishRate;
  private Duration replenishPeriod;

  private boolean global; // global or route-level

  private RedisClient redisClient;

  @PostConstruct
  public void fetchRedisClient() {
    try {
      redisClient = (RedisClient) FieldUtils.getFieldValue(lettuceConnectionFactory, "client");
    } catch (IllegalAccessException e) {
      throw new UnsupportedOperationException("Could not get redisClient");
    }
  }

  private Response POSITIVE = new Response(true, Collections.emptyMap());
  private Response NEGATIVE = new Response(false, Collections.emptyMap());

  @Override
  public Mono<Response> isAllowed(String routeId, String key) {

    final long currentSeconds = Instant.now().getEpochSecond();
    final long windowSeconds = replenishPeriod.toSeconds();
    final long windowId = currentSeconds / windowSeconds;
    final String redisKey =
        String.format(
            "core.service.gateway.rate-limiter/%s/%s/%s/%s",
            global ? "GLOBAL" : routeId, replenishPeriod.toString(), key, windowId);

    log.info("rateLimiter redisKey: {}", redisKey);

    RedisReactiveCommands<String, String> reactive = redisClient.connect().reactive();
    return reactive
        .multi()
        .flatMap(
            result -> {
              reactive.incr(redisKey).subscribe();
              //              reactive.time().subscribe();
              reactive.expire(redisKey, windowSeconds).subscribe();
              return reactive.exec();
            })
        .map(
            txResult -> {
              //          String secondsText = ((List<String>)txResult.get(1)).get(0);
              return (Long) txResult.get(0);
            })
        .onErrorResume(
            (ex) -> {
              log.error("Redis command execution error.", ex);
              reactive.discard().subscribe();
              return Mono.just(0L);
            })
        .map(count -> count <= replenishRate)
        .map(allowed -> allowed ? POSITIVE : NEGATIVE);
  }

  @Override
  public Map<String, SimpleRateLimiterConfig> getConfig() {
    return Collections.emptyMap();
  }

  @Override
  public Class<SimpleRateLimiterConfig> getConfigClass() {
    return SimpleRateLimiterConfig.class;
  }

  @Override
  public SimpleRateLimiterConfig newConfig() {
    return new SimpleRateLimiterConfig();
  }

  @Data
  @NoArgsConstructor
  static class SimpleRateLimiterConfig {}
}
