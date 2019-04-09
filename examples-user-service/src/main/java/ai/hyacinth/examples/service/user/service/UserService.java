package ai.hyacinth.examples.service.user.service;

import ai.hyacinth.core.service.web.common.payload.AuthenticationResult;
import ai.hyacinth.examples.service.user.dto.UserAuthenticationRequest;
import ai.hyacinth.examples.service.user.dto.UserCreationRequest;
import ai.hyacinth.examples.service.user.dto.UserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.util.concurrent.ListenableFuture;

public interface UserService {
  UserInfo createUser(UserCreationRequest userBo);

  UserInfo findUserByName(String name);

  UserInfo findUserById(Long id);

  AuthenticationResult login(UserAuthenticationRequest userAuthenticationRequest);

  Authentication authenticate(UserAuthenticationRequest userAuthenticationRequest);

  ListenableFuture<Long> countingUsers();

  UserInfo setUserPortrait(Long userId, byte[] portrait);
}
