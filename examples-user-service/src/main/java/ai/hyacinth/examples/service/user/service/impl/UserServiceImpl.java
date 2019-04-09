package ai.hyacinth.examples.service.user.service.impl;

import ai.hyacinth.core.service.web.common.ServiceApiException;
import ai.hyacinth.core.service.web.common.payload.AuthenticationResult;
import ai.hyacinth.examples.service.user.domain.User;
import ai.hyacinth.examples.service.user.constants.UserType;
import ai.hyacinth.examples.service.user.dto.UserAuthenticationRequest;
import ai.hyacinth.examples.service.user.dto.UserCreationRequest;
import ai.hyacinth.examples.service.user.dto.UserInfo;
import ai.hyacinth.examples.service.user.repo.UserRepo;
import ai.hyacinth.examples.service.user.service.UserRoleConstants;
import ai.hyacinth.examples.service.user.service.UserService;
import ai.hyacinth.examples.service.user.service.DefaultUserDetails;
import com.google.common.collect.Lists;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

  @Autowired private UserRepo userRepo;

  @Autowired private PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public UserInfo createUser(UserCreationRequest userBo) {
    if (StringUtils.isEmpty(userBo.getPassword())) {
      throw new ServiceApiException(UserServiceErrorCode.EMPTY_PASSWORD);
    }
    if (userRepo.findByUsername(userBo.getUsername()) != null) {
      throw new ServiceApiException(UserServiceErrorCode.USER_EXISTS);
    }

    User user = new User();
    user.setUsername(userBo.getUsername());
    user.setPassword(passwordEncoder.encode(userBo.getPassword()));
    user.setRoles(
        Lists.newArrayList(UserRoleConstants.ROLE_USER, UserRoleConstants.ROLE_API));
    user.setUserType(UserType.PUBLIC);
    user.setBirthDate(userBo.getBirthDate());
    userRepo.save(user);
    return toUserInfo(user);
  }

  @Override
  @Cacheable("user")
  @Transactional(readOnly = true)
  public UserInfo findUserByName(String name) {
    return toUserInfo(userRepo.findByUsername(name));
  }

  @Override
  public UserInfo findUserById(@NotNull Long id) {
    return toUserInfo(userRepo.findById(id));

  }

  private UserInfo toUserInfo(Optional<User> byId) {
    return toUserInfo(byId.orElse(null));
  }

  private UserInfo toUserInfo(User user) {
    if (user != null) {
      UserInfo bo = new UserInfo();
      bo.setUsername(user.getUsername());
      bo.setBirthDate(user.getBirthDate());
      bo.setUserType(user.getUserType());
      bo.setPortraitAvailable(user.getPortrait() != null);
      return bo;
    } else {
      throw new ServiceApiException(UserServiceErrorCode.NO_SUCH_USER);
    }
  }

  @Async
  @Override
  @Transactional(readOnly = true)
  public ListenableFuture<Long> countingUsers() {
    long numUsers = userRepo.count();
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      //
    }
    log.info("in async numUsers: {}", numUsers);
    return new AsyncResult<Long>(numUsers);
  }

  @Override
  @Transactional
  public UserInfo setUserPortrait(Long userId, byte[] portrait) {
    User user = userRepo.getOne(userId);
    user.setPortrait(portrait);
    return toUserInfo(user);
  }

  private UserInfo getUser(Long userId) {
    Optional<UserInfo> userInfo = userRepo.findById(userId).map(this::toUserInfo);
    return userInfo.orElseThrow(() -> new ServiceApiException(UserServiceErrorCode.NO_SUCH_USER));
  }

  @Autowired private AuthenticationManager authenticationManager;

  @Override
  public AuthenticationResult<Long> login(UserAuthenticationRequest userAuthenticationRequest) {
    Authentication authentication = this.authenticate(userAuthenticationRequest);
    DefaultUserDetails userDetails = (DefaultUserDetails) authentication.getPrincipal();
    AuthenticationResult<Long> payload = new AuthenticationResult<>();
    payload.setPrincipalId(userDetails.getUserId());
    payload.setAuthorities(
        userDetails.getAuthorities().stream()
            .map(SimpleGrantedAuthority::getAuthority)
            .collect(Collectors.toList()));
    return payload;
  }

  @Override
  public Authentication authenticate(UserAuthenticationRequest userAuthenticationRequest) {
    try {
      UsernamePasswordAuthenticationToken token =
          new UsernamePasswordAuthenticationToken(
              userAuthenticationRequest.getUsername(), userAuthenticationRequest.getPassword());
      Authentication authentication = authenticationManager.authenticate(token);
      return authentication;
    } catch (AuthenticationException aue) {
      throw new ServiceApiException(UserServiceErrorCode.USER_PASSWORD_MISMATCH);
    }
  }
}
