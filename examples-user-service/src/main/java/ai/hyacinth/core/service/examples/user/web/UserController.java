package ai.hyacinth.core.service.examples.user.web;

import ai.hyacinth.core.service.web.common.error.CommonServiceErrorCode;
import ai.hyacinth.core.service.examples.user.api.UserApi;
import ai.hyacinth.core.service.examples.user.dto.UserAuthenticationRequest;
import ai.hyacinth.core.service.examples.user.dto.UserCreationRequest;
import ai.hyacinth.core.service.examples.user.dto.UserInfo;
import ai.hyacinth.core.service.examples.user.service.UserService;
import ai.hyacinth.core.service.web.common.ServiceApiConstants;
import ai.hyacinth.core.service.web.common.ServiceApiException;
import ai.hyacinth.core.service.web.common.payload.AuthenticationResult;
import io.swagger.annotations.Api;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@Api(value = "User Api")
public class UserController implements UserApi {

  @Autowired private UserService userService;

  @Override
  public UserInfo createUser(UserCreationRequest userCreationRequest) {
    userService
        .countingUsers()
        .addCallback(
            (numUsers) -> {
              log.info("completed, numUsers: {}", numUsers);
            },
            (t) -> {
              log.error("error in job", t);
            });
    return userService.createUser(userCreationRequest);
  }

  //  @RequestMapping(value = {"/authentication/auth"}, method = RequestMethod.POST)
  //  public Authentication authenticate(
  //      @Validated @RequestBody UserAuthenticationRequest userAuthenticationRequest) {
  //    Authentication authenticate = userService.authenticate(userAuthenticationRequest);
  //    SecurityContextHolder.getContext().setAuthentication(authenticate);
  //    return authenticate;
  //  }

  @RequestMapping(
      value = {"/authentication/login"},
      method = RequestMethod.POST)
  public AuthenticationResult login(
      @Validated @RequestBody UserAuthenticationRequest userAuthenticationRequest) {
    return userService.login(userAuthenticationRequest);
  }

  @RequestMapping(
      value = {"/users/current"},
      method = RequestMethod.GET)
  public UserInfo current(
      @RequestHeader(ServiceApiConstants.HTTP_HEADER_AUTHENTICATED_PRINCIPLE) Long userId) {
    return userService.findUserById(userId);
  }

  @Override
  public UserInfo findUserByUsername(String username) {
    return userService.findUserByUsername(username);
  }

  @Override
  public UserInfo findUserById(Long userId) {
    return userService.findUserById(userId);
  }

  @PostMapping("/users/{userId}/portrait")
  public UserInfo setUserPortrait(
      @PathVariable Long userId, @RequestParam("portrait") MultipartFile file) {
    String name = file.getName();
    String originalFilename = file.getOriginalFilename();
    long fileSize = file.getSize();
    byte[] content = null;
    try {
      content = file.getBytes();
    } catch (IOException e) {
      throw new ServiceApiException(
          CommonServiceErrorCode.NETWORK_ERROR, "cannot read portrait content");
    }
    log.info("set user portrait, {} {} = {}", file, originalFilename, fileSize);
    return userService.setUserPortrait(userId, content);
  }
}
