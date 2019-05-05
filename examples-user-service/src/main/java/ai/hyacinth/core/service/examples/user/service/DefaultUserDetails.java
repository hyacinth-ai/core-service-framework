package ai.hyacinth.core.service.examples.user.service;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@NoArgsConstructor
public class DefaultUserDetails implements UserDetails, CredentialsContainer {
  private static final long serialVersionUID = -6403739338297548944L;

  private boolean accountNonLocked;
  private boolean accountNonExpired;
  private boolean credentialsNonExpired;
  private boolean enabled;
  private String username;
  private String password;
  private Long userId;
  private List<SimpleGrantedAuthority> authorities = new ArrayList<>();

  public void eraseCredentials() {
    setPassword(null);
  }
}
