package ai.hyacinth.core.service.gateway.server.ratelimiter;

import java.security.Principal;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Not used now any more. The original default KeyResolver is effective. Once it generates a
 * "ANONYMOUS" tag for statistics in RateLimiter.
 */
// @Component
// @Primary
public class SimpleKeyResolver implements KeyResolver {
  private static final String ANONYMOUS = "ANONYMOUS";

  public static boolean isAnonymous(String key) {
    return key != null && key.startsWith(SimpleKeyResolver.ANONYMOUS);
  }

  @Override
  public Mono<String> resolve(ServerWebExchange exchange) {
    //    + "/" + exchange.getRequest().getRemoteAddress().getHostString()
    return exchange.getPrincipal().map(Principal::getName).switchIfEmpty(Mono.just(ANONYMOUS));
  }
}
