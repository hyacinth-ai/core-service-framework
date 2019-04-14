package ai.hyacinth.core.service.gateway.server.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * do not initialise this bean as @Component
 * Otherwise, it is a global Webflux @WebFilter instance.
 */
public class JwtAuthenticationWebFilter extends AuthenticationWebFilter {
  @Autowired
  private JwtService jwtService;

  public JwtAuthenticationWebFilter() {
    super(new JwtAuthenticationManager());
    this.setServerAuthenticationConverter(new JwtAuthenticationConverter());
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    return super.filter(exchange, chain);
  }

  private class JwtAuthenticationConverter implements ServerAuthenticationConverter {
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
      String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
      Authentication data = jwtService.parseHeader(authHeader);
      return data == null ? Mono.empty() : Mono.just(data);
    }
  }
}
