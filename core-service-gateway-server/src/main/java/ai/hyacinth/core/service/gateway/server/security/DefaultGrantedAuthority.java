package ai.hyacinth.core.service.gateway.server.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefaultGrantedAuthority implements GrantedAuthority {
  private String authority;
}
