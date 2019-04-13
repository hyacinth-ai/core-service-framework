package ai.hyacinth.core.service.gateway.server.route;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

import ai.hyacinth.core.service.gateway.server.configprops.GatewayServerProperties;
import ai.hyacinth.core.service.gateway.server.configprops.ResponsePostProcessingType;
import ai.hyacinth.core.service.gateway.server.payload.ApiSuccessPayload;
import ai.hyacinth.core.service.gateway.server.security.DefaultAuthentication;
import ai.hyacinth.core.service.gateway.server.security.DefaultGrantedAuthority;
import ai.hyacinth.core.service.web.common.ServiceApiConstants;
import ai.hyacinth.core.service.web.common.payload.AuthenticationResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
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

  private static final String JWT_CLAIM_AUTHORITIES = "authority";
  private static final String JWT_CLAIM_PRINCIPAL = "principal";

  @Autowired private ObjectMapper mapper;

  @Autowired private GatewayServerProperties gatewayConfig;

  @Autowired private ServerSecurityContextRepository securityContextRepository;

  @Bean
  @RefreshScope
  public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
    Key signingKey = loadJwtSigningKey();

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
                              f.filter(
                                  rewriteRequestPath(rule.getUri())); // setPath(rule.getUri());
                              f.filter(rewriteRequestPrincipalHeader());
                              f.filter(rewriteRequestParams(rule.getRequestParam()));
                              f.filter(
                                  rewriteRequestBody(
                                      rule.getRequestBody(), rule.getRequestBodyJson()));
                              if (rule.getPostProcessing().size() > 0) {
                                f.modifyResponseBody(
                                    String.class,
                                    String.class,
                                    rewriteSuccessPayload(rule.getPostProcessing(), signingKey));
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

  private Key loadJwtSigningKey() {
    if (gatewayConfig.getJwt().isEnabled()) {
      String key = gatewayConfig.getJwt().getSigningKey();
      if (!StringUtils.isEmpty(key)) {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(key));
      }

      String keyLocation = gatewayConfig.getJwt().getSigningKeyFile();
      if (!StringUtils.isEmpty(keyLocation)) {
        Resource resource = resourceLoader.getResource(keyLocation);
        try {
          byte[] content = resource.getInputStream().readAllBytes();
          return Keys.hmacShaKeyFor(content);
        } catch (IOException e) {
          log.error("cannot read jwt signing key", e);
          throw new RuntimeException(e);
        }
      }
    }
    return null;
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

  private GatewayFilter rewriteRequestPrincipalHeader() {
    return (exchange, chain) ->
        ReactiveSecurityContextHolder.getContext()
            .map(context -> context.getAuthentication().getName())
            .map(principleId -> setPrincipleHeader(exchange, principleId))
            .map(request -> exchange.mutate().request(request).build())
            .defaultIfEmpty(removePassingInPrincipleHeader(exchange))
            .flatMap(chain::filter);
  }

  private ServerWebExchange removePassingInPrincipleHeader(ServerWebExchange exchange) {
    boolean hacked =
        exchange
                .getRequest()
                .getHeaders()
                .get(ServiceApiConstants.HEADER_NAME_AUTHENTICATED_PRINCIPLE)
            != null;
    if (hacked) {
      return exchange
          .mutate()
          .request(
              exchange
                  .getRequest()
                  .mutate()
                  .headers(
                      headers -> {
                        headers.remove(ServiceApiConstants.HEADER_NAME_AUTHENTICATED_PRINCIPLE);
                      })
                  .build())
          .build();
    } else {
      return exchange;
    }
  }

  private ServerHttpRequest setPrincipleHeader(ServerWebExchange exchange, String principleId) {
    return exchange
        .getRequest()
        .mutate()
        .headers(
            headers -> {
              headers.set(ServiceApiConstants.HEADER_NAME_AUTHENTICATED_PRINCIPLE, principleId);
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
              new ServerHttpRequestDecorator(exchange.getRequest()) {
                private int contentLength;

                @Override
                public HttpHeaders getHeaders() {
                  HttpHeaders httpHeaders = new HttpHeaders();
                  httpHeaders.putAll(getDelegate().getHeaders());
                  if (contentLength > 0) {
                    httpHeaders.setContentLength(contentLength);
                  } else {
                    httpHeaders.remove(HttpHeaders.CONTENT_LENGTH);
                  }
                  return httpHeaders;
                }

                @Override
                public Flux<DataBuffer> getBody() {
                  Flux<InputStream> m1 =
                      getDelegate()
                          .getBody()
                          .buffer()
                          .map(
                              df ->
                                  exchange.getResponse().bufferFactory().join(df).asInputStream());
                  Mono<Object> m2 =
                      ReactiveSecurityContextHolder.getContext()
                          .map(context -> context.getAuthentication().getPrincipal())
                          .defaultIfEmpty("");
                  return Flux.zip(m1, Flux.from(m2))
                      .map(
                          (tuple) -> {
                            try {
                              Map originalBody = mapper.readValue(tuple.getT1(), Map.class);
                              requestBody.forEach(
                                  (key, value) -> {
                                    Object jsonValue = value;
                                    if (value instanceof String) {
                                      String valueText = (String) value;
                                      boolean placeholderOnly =
                                          valueText.equals(PRINCIPAL_PLACEHOLDER_VAR);
                                      if (placeholderOnly) {
                                        jsonValue = tuple.getT2();
                                      } else {
                                        jsonValue =
                                            valueText.replace(
                                                PRINCIPAL_PLACEHOLDER_VAR,
                                                String.valueOf(tuple.getT2()));
                                      }
                                    }
                                    originalBody.put(key, jsonValue);
                                  });
                              byte[] content = new byte[0];
                              content = mapper.writeValueAsBytes(originalBody);
                              this.contentLength = content.length;
                              return exchange.getResponse().bufferFactory().wrap(content);
                            } catch (Exception ex) {
                              log.error("request payload json injection failed.", ex);
                              throw new IllegalStateException(); // abort
                            }
                          });
                }
              };
          return chain.filter(exchange.mutate().request(decorator).build());
        }
      }
      return chain.filter(exchange);
    };
  }

  private static final String PRINCIPAL_PLACEHOLDER_VAR =
      "${" + ServiceApiConstants.HEADER_NAME_AUTHENTICATED_PRINCIPLE + "}";

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
      final List<ResponsePostProcessingType> processingList, Key jwtSigningKey) {
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
                  String token =
                      Jwts.builder()
                          .signWith(jwtSigningKey, gatewayConfig.getJwt().getSignatureAlgorithm())
                          .setHeaderParam("typ", "JWT")
                          .setSubject(authentication.getName())
                          .setExpiration(
                              new Date(
                                  System.currentTimeMillis()
                                      + gatewayConfig.getJwt().getExpiration().toMillis()))
                          .claim(JWT_CLAIM_AUTHORITIES, payload.getAuthorities())
                          .claim(JWT_CLAIM_PRINCIPAL, authentication.getPrincipal())
                          .compact();
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
      }

      log.warn("unknown status code: {}, content: {}", statusCode.toString(), input);
      return Mono.just(input);
    };
  }

  @Autowired private ResourceLoader resourceLoader;

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
