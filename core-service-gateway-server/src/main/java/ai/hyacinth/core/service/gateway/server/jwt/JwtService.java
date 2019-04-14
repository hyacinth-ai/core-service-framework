package ai.hyacinth.core.service.gateway.server.jwt;

import ai.hyacinth.core.service.gateway.server.configprops.GatewayServerProperties;
import ai.hyacinth.core.service.gateway.server.security.DefaultAuthentication;
import ai.hyacinth.core.service.gateway.server.security.DefaultGrantedAuthority;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.io.IOException;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class JwtService {

  private static final String JWT_CLAIM_AUTHORITIES = "authority";
  private static final String JWT_CLAIM_PRINCIPAL = "principal";
  private static final String JWT_CLAIM_VERSION = "version";

  @Autowired private GatewayServerProperties gatewayConfig;

  @Autowired private ResourceLoader resourceLoader;

  private Key jwtSigningKey;

  public JwtService() {}

  @PostConstruct
  public void loadKeys() {
    this.jwtSigningKey = loadJwtSigningKey();
  }

  public Authentication parseHeader(String authHeader) {
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String bearerToken = authHeader.substring(7).trim();
      return parseToken(bearerToken);
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public Authentication parseToken(String token) {
    try {
      Jws<Claims> claims = Jwts.parser().setSigningKey(jwtSigningKey).parseClaimsJws(token);
      if (validateClaims(claims)) {
        DefaultAuthentication auth = new DefaultAuthentication();
        auth.setDetails(claims);
        auth.setName(claims.getBody().getSubject());
        auth.setAuthenticated(false);
        auth.setPrincipal(claims.getBody().get(JWT_CLAIM_PRINCIPAL));
        auth.setAuthorities(
            ((List<String>) claims.getBody().get(JWT_CLAIM_AUTHORITIES))
                .stream().map(DefaultGrantedAuthority::new).collect(Collectors.toList()));
        return auth;
      }
    } catch (Exception ex) {
      log.warn("token parsing error", ex);
      // any exception is regarded as token error and ignore
    }
    return null;
  }

  private boolean validateClaims(Jws<Claims> claims) {
    if (gatewayConfig.getJwt().getTokenVersion() != null) {
      Integer tokenVersion = claims.getBody().get(JWT_CLAIM_VERSION, Integer.class);
      if (tokenVersion == null || !tokenVersion.equals(gatewayConfig.getJwt().getTokenVersion())) {
        return false;
      }
    }

    if (claims.getBody().getExpiration().after(new Date())) {
      return true;
    }

    return false;
  }

  public String createToken(Authentication authentication) {
    return Jwts.builder()
        .signWith(jwtSigningKey, gatewayConfig.getJwt().getSignatureAlgorithm())
        .setHeaderParam("typ", "JWT")
        .setSubject(authentication.getName())
        .setIssuedAt(new Date())
        .setIssuer(gatewayConfig.getJwt().getIssuer())
        .setExpiration(
            new Date(
                System.currentTimeMillis() + gatewayConfig.getJwt().getExpiration().toMillis()))
        .claim(
            JWT_CLAIM_AUTHORITIES,
            authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()))
        .claim(JWT_CLAIM_PRINCIPAL, authentication.getPrincipal())
        .claim(JWT_CLAIM_VERSION, gatewayConfig.getJwt().getTokenVersion())
        .compact();
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
}
