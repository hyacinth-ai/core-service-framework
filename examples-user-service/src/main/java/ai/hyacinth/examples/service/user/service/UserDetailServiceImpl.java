package ai.hyacinth.examples.service.user.service;

import ai.hyacinth.examples.service.user.domain.User;
import ai.hyacinth.examples.service.user.repo.UserRepo;
import java.util.Date;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

  @Autowired private UserRepo userRepo;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepo.findByUsername(username);
    if (user == null) {
      throw new UsernameNotFoundException(username);
    }

    DefaultUserDetails userDetails = new DefaultUserDetails();
    userDetails.setUserId(user.getId());
    userDetails.setUsername(user.getUsername());
    userDetails.setPassword(user.getPassword());
    userDetails.setAccountNonExpired(ifNonExpired(user.getAccountExpirationDate()));
    userDetails.setCredentialsNonExpired(ifNonExpired((user.getCredentialExpirationDate())));
    userDetails.setAccountNonLocked(!user.isLocked());
    userDetails.setEnabled(!user.isDisabled());
    userDetails.setAuthorities(
        user.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

    return userDetails;
  }

  private boolean ifNonExpired(Date expiration) {
    return expiration == null || expiration.after(new Date());
  }
}
