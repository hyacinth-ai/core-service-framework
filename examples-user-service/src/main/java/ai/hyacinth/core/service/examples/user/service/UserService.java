package ai.hyacinth.core.service.examples.user.service;

import ai.hyacinth.core.service.examples.user.dto.UserAuthenticationRequest;
import ai.hyacinth.core.service.examples.user.dto.UserCreationRequest;
import ai.hyacinth.core.service.examples.user.dto.UserInfo;
import ai.hyacinth.core.service.web.common.payload.AuthenticationResult;
import org.springframework.security.core.Authentication;
import org.springframework.util.concurrent.ListenableFuture;

public interface UserService {
  UserInfo createUser(UserCreationRequest userBo);

  UserInfo findUserByUsername(String name);

  UserInfo findUserById(Long id);

  AuthenticationResult login(UserAuthenticationRequest userAuthenticationRequest);

  Authentication authenticate(UserAuthenticationRequest userAuthenticationRequest);

  ListenableFuture<Long> countingUsers();

  UserInfo setUserPortrait(Long userId, byte[] portrait);
}
