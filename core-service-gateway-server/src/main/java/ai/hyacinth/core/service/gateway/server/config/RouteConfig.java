package ai.hyacinth.core.service.gateway.server.config;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

import ai.hyacinth.core.service.gateway.server.configprops.GatewayRateLimiterProperties;
import ai.hyacinth.core.service.gateway.server.configprops.GatewayServerProperties;
import ai.hyacinth.core.service.gateway.server.configprops.ResponsePostProcessingType;
import ai.hyacinth.core.service.gateway.server.jwt.JwtService;
import ai.hyacinth.core.service.gateway.server.payload.ApiSuccessPayload;
import ai.hyacinth.core.service.gateway.server.ratelimiter.SimpleRateLimiter;
import ai.hyacinth.core.service.gateway.server.security.DefaultAuthentication;
import ai.hyacinth.core.service.gateway.server.security.DefaultGrantedAuthority;
import ai.hyacinth.core.service.web.common.ServiceApiConstants;
import ai.hyacinth.core.service.web.common.payload.AuthenticationResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder.Builder;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class RouteConfig {

  @Autowired private ObjectMapper mapper;

  @Autowired private ServerSecurityContextRepository securityContextRepository;

  @Autowired private JwtService jwtService;

  @Autowired private ResourceLoader resourceLoader;

  @Autowired private ApplicationContext applicationContext;

  @Bean
  @RefreshScope
  public RouteLocator customRouteLocator(
      RouteLocatorBuilder builder, GatewayServerProperties gatewayConfig) {

    final SimpleRateLimiter globalRateLimiter = buildRateLimiter(gatewayConfig.getRateLimiter(), true);

    Builder routes = builder.routes();
    gatewayConfig.getRules().stream()
        //        .filter(rule -> !StringUtils.isEmpty(rule.getService()))
        .forEach(
            rule -> {
              routes.route(
                  route -> {
                    PredicateSpec start = route;
                    if (rule.getMethod() != null) {
                      start = route.method(rule.getMethod()).and();
                    }
                    return start
                        .path(rule.getPath())
                        .filters(
                            f -> {
                              final SimpleRateLimiter ruleRateLimiter = buildRateLimiter(rule.getRateLimiter(), false);
                              if (ruleRateLimiter != null) {
                                f.requestRateLimiter((config) -> {
                                  config.setRateLimiter(ruleRateLimiter);
                                });
                              }

                              if (globalRateLimiter != null) {
                                f.requestRateLimiter((config) -> {
                                  config.setRateLimiter(globalRateLimiter);
                                  config.setDenyEmptyKey(false);
                                });
                              }

                              f.filter(rewriteRequestPath(rule.getUri()));
                              // setPath(rule.getUri());
                              f.filter(removeSensitiveHttpHeaders());
                              f.filter(rewriteRequestHeader());
                              f.filter(rewriteRequestParams(rule.getRequestParam()));
                              f.filter(
                                  rewriteRequestBody(
                                      rule.getRequestBody(), rule.getRequestBodyJson()));
                              if (rule.getPostProcessing().size() > 0) {
                                f.modifyResponseBody(
                                    String.class,
                                    String.class,
                                    rewriteSuccessPayload(rule.getPostProcessing()));
                              }
                              return f;
                            })
                        .uri(
                            StringUtils.isEmpty(rule.getService())
                                ? "forward:///"
                                : "lb://" + rule.getService());
                  });
            });
    return routes.build();
  }

  private SimpleRateLimiter buildRateLimiter(GatewayRateLimiterProperties rateLimiterProperties, boolean global) {
    SimpleRateLimiter rateLimiter = null;
    if (rateLimiterProperties.getReplenishRate() != null) {
      rateLimiter = applicationContext.getBean(SimpleRateLimiter.class);
      rateLimiter.setGlobal(global);
      rateLimiter.setReplenishPeriod(rateLimiterProperties.getReplenishPeriod());
      rateLimiter.setReplenishRate(rateLimiterProperties.getReplenishRate());
    }
    return rateLimiter;
  }

  private GatewayFilter rewriteRequestPath(final String uriTemplateText) {
    // please refer to class SetPathGatewayFilterFactory
    return (exchange, chain) ->
        ReactiveSecurityContextHolder.getContext()
            .map(context -> context.getAuthentication().getPrincipal())
            .defaultIfEmpty("")
            .map(String::valueOf)
            .map(
                principal -> {
                  ServerHttpRequest req = exchange.getRequest();
                  ServerWebExchangeUtils.addOriginalRequestUrl(exchange, req.getURI());

                  final UriTemplate uriTemplate =
                      new UriTemplate(
                          uriTemplateText.replace(PRINCIPAL_PLACEHOLDER_VAR, principal));

                  Map<String, String> uriVariables =
                      ServerWebExchangeUtils.getUriTemplateVariables(exchange);
                  URI uri = uriTemplate.expand(uriVariables);

                  exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, uri);
                  String newPath = uri.getRawPath();

                  return req.mutate().path(newPath).build();
                })
            .flatMap(
                request -> {
                  return chain.filter(exchange.mutate().request(request).build());
                });
  }

  private GatewayFilter rewriteRequestHeader() {
    return (exchange, chain) ->
        ReactiveSecurityContextHolder.getContext()
            .map(context -> String.valueOf(context.getAuthentication().getPrincipal()))
            .map(principle -> setPrincipleHeader(exchange, principle))
            .map(request -> exchange.mutate().request(request).build())
            .defaultIfEmpty(exchange)
            .flatMap(chain::filter);
  }

  private GatewayFilter removeSensitiveHttpHeaders() {
    return (exchange, chain) ->
        Mono.fromCallable(
                () ->
                    exchange
                        .mutate()
                        .request(exchange.getRequest().mutate().headers(this::resetHeaders).build())
                        .build())
            .flatMap(chain::filter);
  }

  private void resetHeaders(HttpHeaders headers) {
    headers.remove(ServiceApiConstants.HTTP_HEADER_AUTHENTICATED_PRINCIPLE);
    headers.remove(HttpHeaders.AUTHORIZATION);
    headers.remove(HttpHeaders.COOKIE);
    headers.remove(HttpHeaders.SET_COOKIE);
  }

  private ServerHttpRequest setPrincipleHeader(ServerWebExchange exchange, String principleId) {
    return exchange
        .getRequest()
        .mutate()
        .headers(
            headers -> {
              headers.set(ServiceApiConstants.HTTP_HEADER_AUTHENTICATED_PRINCIPLE, principleId);
            })
        .build();
  }

  @SuppressWarnings("unchecked")
  private GatewayFilter rewriteRequestBody(
      final Map<String, Object> requestBody, @Nullable String requestBodyJson) {
    return (exchange, chain) -> {
      if (requestBody.size() > 0 || requestBodyJson != null) {
        MediaType mediaType = exchange.getRequest().getHeaders().getContentType();
        if (isJsonType(mediaType)) {
          ServerHttpRequestDecorator decorator =
              createRequestBodyRewriteDecorator(requestBody, exchange);
          return chain.filter(exchange.mutate().request(decorator).build());
        }
      }
      return chain.filter(exchange);
    };
  }

  private ServerHttpRequestDecorator createRequestBodyRewriteDecorator(
      Map<String, Object> requestBody, ServerWebExchange exchange) {
    return new ServerHttpRequestDecorator(exchange.getRequest()) {
      @Override
      public HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.putAll(getDelegate().getHeaders());
        httpHeaders.remove(HttpHeaders.CONTENT_LENGTH);
        return httpHeaders;
      }

      @Override
      public Flux<DataBuffer> getBody() {
        Flux<InputStream> m1 =
            getDelegate()
                .getBody()
                .buffer()
                .map(df -> exchange.getResponse().bufferFactory().join(df).asInputStream());
        Mono<Object> m2 =
            ReactiveSecurityContextHolder.getContext()
                .map(context -> context.getAuthentication().getPrincipal())
                .defaultIfEmpty("");
        return Flux.zip(m1, Flux.from(m2))
            .map(
                (tuple) -> {
                  try {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> originalBody = mapper.readValue(tuple.getT1(), Map.class);

                    requestBody.forEach(
                        (key, value) -> {
                          Object jsonValue = value;
                          if (value instanceof String) {
                            String valueText = (String) value;
                            boolean placeholderOnly = valueText.equals(PRINCIPAL_PLACEHOLDER_VAR);
                            if (placeholderOnly) {
                              jsonValue = tuple.getT2();
                            } else {
                              jsonValue =
                                  valueText.replace(
                                      PRINCIPAL_PLACEHOLDER_VAR, String.valueOf(tuple.getT2()));
                            }
                          }
                          originalBody.put(key, jsonValue);
                        });
                    byte[] content = mapper.writeValueAsBytes(originalBody);
                    return exchange.getResponse().bufferFactory().wrap(content);
                  } catch (Exception ex) {
                    log.error("request payload json injection failed.", ex);
                    throw new IllegalStateException(); // abort
                  }
                });
      }
    };
  }

  private static final String PRINCIPAL_PLACEHOLDER_VAR =
      "${" + ServiceApiConstants.HTTP_HEADER_AUTHENTICATED_PRINCIPLE + "}";

  private GatewayFilter rewriteRequestParams(final Map<String, String> requestParameters) {
    return (exchange, chain) -> {
      if (requestParameters.size() > 0) {
        return ReactiveSecurityContextHolder.getContext()
            .map(context -> context.getAuthentication().getPrincipal())
            .map(String::valueOf)
            .defaultIfEmpty("")
            .map(
                principal -> {
                  MultiValueMap<String, String> queryParams =
                      exchange.getRequest().getQueryParams();
                  LinkedMultiValueMap<String, String> mutatedQueryParams =
                      new LinkedMultiValueMap<>(queryParams);

                  requestParameters.forEach(
                      (key, value) -> {
                        mutatedQueryParams.remove(key);
                        mutatedQueryParams.add(
                            key,
                            String.valueOf(value).replace(PRINCIPAL_PLACEHOLDER_VAR, principal));
                      });

                  URI mutatedUri =
                      UriComponentsBuilder.fromUri(exchange.getRequest().getURI())
                          .replaceQueryParams(mutatedQueryParams)
                          .build()
                          .toUri();

                  return mutatedUri;
                })
            .map(mutatedUri -> exchange.getRequest().mutate().uri(mutatedUri).build())
            .flatMap(
                mutatedRequest -> chain.filter(exchange.mutate().request(mutatedRequest).build()));
      } else {
        return chain.filter(exchange);
      }
    };
  }

  private RewriteFunction<String, String> rewriteSuccessPayload(
      final List<ResponsePostProcessingType> processingList) {
    boolean authCookie = processingList.contains(ResponsePostProcessingType.AUTHENTICATION_COOKIE);
    boolean authJwt = processingList.contains(ResponsePostProcessingType.AUTHENTICATION_JWT);
    boolean apiWrapping = processingList.contains(ResponsePostProcessingType.API);

    boolean payloadAsAuth = authCookie || authJwt;

    return (exchange, input) -> {
      HttpStatus statusCode = exchange.getResponse().getStatusCode();
      if (statusCode.is2xxSuccessful()) {
        HttpHeaders headers = exchange.getResponse().getHeaders();
        MediaType contentType = headers.getContentType();
        if (isJsonType(contentType)) {
          if (input.length() > 0) {

            try {
              Mono<Void> saveMono = Mono.empty();
              String data = input; // fast-mode raw json string

              if (payloadAsAuth) {

                AuthenticationResult<?> payload = fromJson(input, AuthenticationResult.class);

                DefaultAuthentication authentication = new DefaultAuthentication();
                authentication.setAuthenticated(true);
                authentication.setPrincipal(payload.getPrincipal());
                authentication.setName(String.valueOf(payload.getPrincipal()));
                authentication.setAuthorities(
                    payload.getAuthorities().stream()
                        .map(DefaultGrantedAuthority::new)
                        .collect(Collectors.toList()));

                if (authCookie) {
                  SecurityContext securityContext = new SecurityContextImpl(authentication);
                  saveMono =
                      securityContextRepository
                          .save(exchange, securityContext)
                          .subscriberContext(
                              ReactiveSecurityContextHolder.withSecurityContext(
                                  Mono.just(securityContext)));
                  data = null; // clear authentication details
                }

                if (authJwt) {
                  String token = jwtService.createToken(authentication);
                  data = String.format("{\"token\":\"%s\"}", token);
                }
              }

              if (apiWrapping) {
                ApiSuccessPayload response = new ApiSuccessPayload();
                response.setData(data);
                data = mapper.writeValueAsString(response);
              }

              return saveMono.then(Mono.just(data));

            } catch (JsonProcessingException e) {
              log.error("error while wrapping successful response payload: {}", input);
            }
          }
        }
      } else if (statusCode.is4xxClientError()) {
        // always set http status to 2xx while api calls pass through gateway
        // {"status":"error"} is set in response payload when error occurs in service endpoint.
        // refer to web-support module
        exchange.getResponse().setStatusCode(HttpStatus.OK);
      } else {
        log.warn("unknown status code: {}, content: {}", statusCode.toString(), input);
      }

      return Mono.just(input);
    };
  }

  private <T> T fromJson(String input, Class<T> inputClass) {
    try {
      return mapper.readValue(input, inputClass);
    } catch (Exception ex) {
      throw new IllegalStateException("input payload cannot be parsed into " + inputClass, ex);
    }
  }

  private boolean isJsonType(MediaType contentType) {
    return contentType != null
        && (contentType.equals(MediaType.APPLICATION_JSON)
            || contentType.equals(MediaType.APPLICATION_JSON_UTF8));
  }
}
