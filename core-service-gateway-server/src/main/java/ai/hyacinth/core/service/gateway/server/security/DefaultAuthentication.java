package ai.hyacinth.core.service.gateway.server.security;

import java.util.ArrayList;
import java.util.Collection;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;

@Data
@NoArgsConstructor
public class DefaultAuthentication implements Authentication {
  private static final long serialVersionUID = 383300673565185225L;

  boolean authenticated;
  Object credentials;
  Object details;
  Object principal;
  String name;
  Collection<DefaultGrantedAuthority> authorities = new ArrayList<>();
}
