package ai.hyacinth.core.service.gateway.server.jwt;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

public class JwtAuthenticationManager implements ReactiveAuthenticationManager {
  public JwtAuthenticationManager() {
  }

  @Override
  public Mono<Authentication> authenticate(Authentication authentication) {
    // for jwt authentication, as long as it is created, it is a valid token.
    authentication.setAuthenticated(true);
    return Mono.just(authentication);
  }
}
