package ai.hyacinth.examples.service.user.web;

import ai.hyacinth.core.service.web.common.ServiceApiConstants;
import ai.hyacinth.core.service.web.common.payload.AuthenticationPayload;
import ai.hyacinth.examples.service.user.api.UserApi;
import ai.hyacinth.examples.service.user.dto.UserAuthenticationRequest;
import ai.hyacinth.examples.service.user.dto.UserCreationRequest;
import ai.hyacinth.examples.service.user.dto.UserInfo;
import ai.hyacinth.examples.service.user.service.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@Api(value = "User Api")
public class UserController implements UserApi {

  @Autowired private UserService userService;

  @Override
  public UserInfo createUser(@Validated @RequestBody UserCreationRequest userCreationRequest) {
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

  @RequestMapping(value = {"/authentication/auth"}, method = RequestMethod.POST)
  public Authentication authenticate(
      @Validated @RequestBody UserAuthenticationRequest userAuthenticationRequest) {
    Authentication authenticate = userService.authenticate(userAuthenticationRequest);
    SecurityContextHolder.getContext().setAuthentication(authenticate);
    return authenticate;
  }

  @RequestMapping(value = {"/authentication/login"}, method = RequestMethod.POST)
  public AuthenticationPayload login(
      @Validated @RequestBody UserAuthenticationRequest userAuthenticationRequest) {
    return userService.login(userAuthenticationRequest);
  }

  @RequestMapping(value = {"/users/current"}, method = RequestMethod.GET)
  public UserInfo current(@RequestHeader(ServiceApiConstants.HEADER_NAME_GATEWAY_PRINCIPLE_ID) Long userId) {
    return userService.findUserById(userId);
  }

  @Override
  public UserInfo findUserByName(String username) {
    return userService.findUserByName(username);
  }

  @Override
  public UserInfo findUserById(@PathVariable Long userId) {
    return userService.findUserById(userId);
  }

  @PostMapping("/users/{userId}/portrait")
  public Map<String, Object> setUserPortrait(@PathVariable String userId, @RequestParam("portrait") MultipartFile file) {
    String name = file.getName();
    String originalFilename = file.getOriginalFilename();
    long fileSize = file.getSize();
    long realSize = 0;
    try {
      byte[] content = file.getBytes();
      realSize = content.length;
    } catch (IOException e) {
      log.error("cannot get portrait content");
    }
    log.info("set user portrait, name: {} = {} of {}", originalFilename, fileSize, realSize);
    Map<String, Object> map = new HashMap<>();
    map.put("name", originalFilename);
    map.put("size", fileSize);
    return map;
  }
}
