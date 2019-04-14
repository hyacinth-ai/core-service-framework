package ai.hyacinth.core.service.gateway.server.configprops;

import io.jsonwebtoken.SignatureAlgorithm;
import java.time.Duration;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
@NoArgsConstructor
@Data
public class GatewayJwtProperties {
  private boolean enabled = false;
  private SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;
  private Duration expiration = Duration.ofHours(1);
  /** key file resource location */
  private String signingKeyFile;
  /** signingKey in base64 string */
  private String signingKey;
  /** optional iss claim */
  private String issuer;
  /** token version to invalidate old token **/
  public Integer tokenVersion;
}
