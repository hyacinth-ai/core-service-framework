package ai.hyacinth.core.service.gateway.server.route;

import ai.hyacinth.core.service.gateway.server.configprops.GatewayServerProperties;
import ai.hyacinth.core.service.gateway.server.configprops.ResponsePostProcessingType;
import ai.hyacinth.core.service.gateway.server.payload.ApiSuccessPayload;
import ai.hyacinth.core.service.gateway.server.security.DefaultAuthentication;
import ai.hyacinth.core.service.gateway.server.security.DefaultGrantedAuthority;
import ai.hyacinth.core.service.web.common.ServiceApiConstants;
import ai.hyacinth.core.service.web.common.payload.AuthenticationPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder.Builder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class RouteConfig {

  private static final String GATEWAY_API_PREFIX = "/api";

  @Autowired private ObjectMapper mapper;

  @Autowired private GatewayServerProperties gatewayConfig;

  @Autowired private ServerSecurityContextRepository securityContextRepository;

  @Bean
  public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
    Builder routes = builder.routes();
    gatewayConfig
        .getRules()
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
                              f.setPath(rule.getUri());
                              //
                              f.filter(rewriteRequestPrincipalHeader());
                              f.filter(rewriteRequestParams(rule.getRequestParameters()));
                              f.filter(rewriteRequestBody(rule.getRequestBody()));
                              if (rule.getPostProcessing().equals(ResponsePostProcessingType.API)) {
                                f.modifyResponseBody(
                                    String.class,
                                    String.class,
                                    rewriteSuccessPayload(rule.isAuthenticationApi()));
                              }
                              return f;
                            })
                        .uri("lb://" + rule.getService());
                  });
            });
    return routes.build();
  }

  private GatewayFilter rewriteRequestPrincipalHeader() {
    return (exchange, chain) ->
        ReactiveSecurityContextHolder.getContext()
            .map(
                context ->
                    context.getAuthentication() != null
                        ? context.getAuthentication().getName()
                        : "")
            .map(
                pId ->
                    exchange
                        .getRequest()
                        .mutate()
                        .headers(
                            headers -> {
                              headers.remove(ServiceApiConstants.HEADER_NAME_GATEWAY_PRINCIPLE_ID); // remove the header value for security
                              headers.set(
                                  ServiceApiConstants.HEADER_NAME_GATEWAY_PRINCIPLE_ID, pId);
                            })
                        .build())
            .flatMap(request -> chain.filter(exchange.mutate().request(request).build()));
  }

  @SuppressWarnings("unchecked")
  private GatewayFilter rewriteRequestBody(final Map<String, String> requestBody) {
    return (exchange, chain) -> {
      if (requestBody.size() > 0) {
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
                  return getDelegate()
                      .getBody()
                      .buffer()
                      .flatMap(
                          (df) -> {
                            try {
                              Map tree =
                                  mapper.readValue(
                                      exchange
                                          .getResponse()
                                          .bufferFactory()
                                          .join(df)
                                          .asInputStream(),
                                      Map.class);
                              requestBody.forEach(tree::put);
                              byte[] content = mapper.writeValueAsBytes(tree);
                              this.contentLength = content.length;
                              return Mono.just(
                                  exchange.getResponse().bufferFactory().wrap(content));
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

  private GatewayFilter rewriteRequestParams(final Map<String, String> requestParameters) {
    return (exchange, chain) -> {
      if (requestParameters.size() > 0) {
        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();

        LinkedMultiValueMap<String, String> mutatedQueryParams =
            new LinkedMultiValueMap<>(queryParams);
        requestParameters.forEach(
            (key, value) -> {
              mutatedQueryParams.remove(key);
              mutatedQueryParams.add(key, value);
            });

        URI mutatedUri =
            UriComponentsBuilder.fromUri(exchange.getRequest().getURI())
                .replaceQueryParams(mutatedQueryParams)
                .build()
                .toUri();
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate().uri(mutatedUri).build();
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
      } else {
        return chain.filter(exchange);
      }
    };
  }

  private RewriteFunction<String, String> rewriteSuccessPayload(
      final boolean authenticationPayload) {
    return (exchange, input) -> {
      HttpStatus statusCode = exchange.getResponse().getStatusCode();
      if (statusCode.is2xxSuccessful()) {
        HttpHeaders headers = exchange.getResponse().getHeaders();
        MediaType contentType = headers.getContentType();
        if (isJsonType(contentType)) {
          if (input.length() > 0) {
            ApiSuccessPayload response = new ApiSuccessPayload();

            try {
              Mono<Void> saveMono;

              if (authenticationPayload) {

                AuthenticationPayload payload = fromJson(input, AuthenticationPayload.class);
                DefaultAuthentication authentication = new DefaultAuthentication();
                authentication.setAuthenticated(true);
                authentication.setPrincipal(payload.getPrincipalId());
                authentication.setName(payload.getPrincipalId());
                authentication.setAuthorities(
                    payload.getAuthorities().stream()
                        .map(DefaultGrantedAuthority::new)
                        .collect(Collectors.toList()));

                SecurityContext securityContext = new SecurityContextImpl(authentication);
                saveMono =
                    securityContextRepository
                        .save(exchange, securityContext)
                        .subscriberContext(
                            ReactiveSecurityContextHolder.withSecurityContext(
                                Mono.just(securityContext)));
                response.setData(null); // do not provide authentication details to caller
              } else {
                saveMono = Mono.empty();
                response.setData(input); // fast-mode raw json string
              }

              String wrappedPayload = mapper.writeValueAsString(response);
              return saveMono.then(Mono.just(wrappedPayload));

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
