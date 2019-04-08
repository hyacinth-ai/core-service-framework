package ai.hyacinth.core.service.gateway.server.route;

import ai.hyacinth.core.service.gateway.server.configprops.GatewaySecurityProperties;
import ai.hyacinth.core.service.gateway.server.configprops.GatewayServerProperties;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec.Access;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.HttpStatusReturningServerLogoutSuccessHandler;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {

  /**
   * ANY is artificial role used to be applied on route rules instead of being assigned on a user.
   */
  private static final String ROLE_ANY = "ANY";

  @Bean
  public ServerSecurityContextRepository serverSecurityContextRepository() {
    return new WebSessionServerSecurityContextRepository();
  }

  @Bean
  public SecurityWebFilterChain configureSecurityFilterChain(
      ServerHttpSecurity http,
      ServerSecurityContextRepository serverSecurityContextRepository,
      GatewayServerProperties gatewayServerProperties) {
    AuthorizeExchangeSpec authExchange = http.authorizeExchange();
    final GatewaySecurityProperties securityProperties = gatewayServerProperties.getSecurity();
    gatewayServerProperties
        .getRules()
        .forEach(
            r -> {
              List<String> ruleRoles =
                  r.getRole().stream().map(String::toUpperCase).collect(Collectors.toList());
              Access xs =
                  r.getMethod() != null
                      ? authExchange.pathMatchers(r.getMethod(), r.getPath())
                      : authExchange.pathMatchers(r.getPath());
              if (ruleRoles.contains(ROLE_ANY)) {
                xs.permitAll();
              } else {
                if (ruleRoles.isEmpty()) {
                  if (securityProperties.isAuthenticatedRequired()) {
                    xs.authenticated();
                  } else {
                    xs.permitAll();
                  }
                } else if (ruleRoles.size() == 1) {
                  xs.hasRole(ruleRoles.get(0));
                } else {
                  xs.access(
                      (authentication, e) ->
                          authentication
                              .filter(Authentication::isAuthenticated)
                              .flatMapIterable(Authentication::getAuthorities)
                              .map(GrantedAuthority::getAuthority)
                              .filter(ruleRoles::contains)
                              .hasElements()
                              .map(AuthorizationDecision::new)
                              .defaultIfEmpty(new AuthorizationDecision(false)));
                }
              }
            });
    authExchange.anyExchange().denyAll();

    http.csrf().disable().httpBasic().disable().formLogin().disable();

    if (!StringUtils.isEmpty(securityProperties.getLogoutUrl())) {
      http.logout()
          .logoutUrl(securityProperties.getLogoutUrl())
          .logoutSuccessHandler(
              new HttpStatusReturningServerLogoutSuccessHandler() {
                @Override
                public Mono<Void> onLogoutSuccess(
                    WebFilterExchange exchange, Authentication authentication) {
                  exchange.getExchange().getResponse().setStatusCode(HttpStatus.OK);
                  return exchange
                      .getExchange()
                      .getResponse()
                      .writeWith(
                          Mono.just(
                              exchange
                                  .getExchange()
                                  .getResponse()
                                  .bufferFactory()
                                  .wrap(securityProperties.getLogoutPayload().getBytes())));
                }
              });
    }

    return http.securityContextRepository(serverSecurityContextRepository).build();
  }
}
