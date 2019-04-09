package ai.hyacinth.examples.service.user.web;

import ai.hyacinth.core.service.web.common.ServiceApiConstants;
import ai.hyacinth.core.service.web.common.payload.AuthenticationPayload;
import ai.hyacinth.examples.service.user.api.UserApi;
import ai.hyacinth.examples.service.user.model.UserAuthenticationRequest;
import ai.hyacinth.examples.service.user.model.UserCreationRequest;
import ai.hyacinth.examples.service.user.model.UserInfo;
import ai.hyacinth.examples.service.user.service.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
    return userService.createUser(userCreationRequest);
  }

  @RequestMapping(value = {"/authentication/auth"}, method = RequestMethod.POST)
  public Authentication authenticate(
      @Validated @RequestBody UserAuthenticationRequest userAuthenticationRequest) {
    Authentication authenticate = userService.authenticate(userAuthenticationRequest);
//    SecurityContextHolder.getContext().setAuthentication();
//    Authentication authentication = userService.authenticate(userAuthenticationRequest)
//    return new UserInfo("any");
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
  public UserInfo findUser(String name) {
    userService
        .countingUsers()
        .addCallback(
            (numUsers) -> {
              log.info("completed, numUsers: {}", numUsers);
            },
            (t) -> {
              log.error("error in job", t);
            });
    return userService.findUser(name);
  }

  @Override
  public UserInfo getUser(@PathVariable Long userId) {
    return userService.getUser(userId);
  }

  @PostMapping("/users/{userId}/portrait")
  public Map<String, Object> uploadUserPortrait(@PathVariable String userId, @RequestParam("portrait") MultipartFile file) {
    String name = file.getName();
    String originalFilename = file.getOriginalFilename();
    long fileSize = file.getSize();
    long realSize = 0;
    try {
      byte[] content = file.getBytes();
      realSize = content.length;
    } catch (IOException e) {
      log.error("cannot get content");
    }
    log.info("upload user portrait, name: {} = {} of {}", originalFilename, fileSize, realSize);
    Map<String, Object> map = new HashMap<>();
    map.put("name", originalFilename);
    map.put("size", fileSize);
    return map;
  }
}
