package ai.hyacinth.examples.service.user.service;

import ai.hyacinth.core.service.web.common.payload.AuthenticationPayload;
import ai.hyacinth.examples.service.user.model.UserAuthenticationRequest;
import ai.hyacinth.examples.service.user.model.UserCreationRequest;
import ai.hyacinth.examples.service.user.model.UserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.util.concurrent.ListenableFuture;

public interface UserService {
  UserInfo createUser(UserCreationRequest userBo);

  UserInfo findUser(String name);

  UserInfo findUserById(Long id);

  AuthenticationPayload login(UserAuthenticationRequest userAuthenticationRequest);

  Authentication authenticate(UserAuthenticationRequest userAuthenticationRequest);

  ListenableFuture<Long> countingUsers();

  UserInfo getUser(Long userId);
}
