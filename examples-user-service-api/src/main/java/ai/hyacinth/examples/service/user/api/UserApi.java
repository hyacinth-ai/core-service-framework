package ai.hyacinth.examples.service.user.api;

import ai.hyacinth.core.service.api.support.config.ServiceApiConfig;
import ai.hyacinth.core.service.web.common.ServiceApiConstants;
import ai.hyacinth.examples.service.user.dto.UserCreationRequest;
import ai.hyacinth.examples.service.user.dto.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user-service", configuration = ServiceApiConfig.class)
@RequestMapping(ServiceApiConstants.API_PREFIX)
public interface UserApi {
  @RequestMapping(value = "/users", method = RequestMethod.POST)
  UserInfo createUser(@Validated @RequestBody UserCreationRequest userCreationRequest);

  @RequestMapping(value = "/users", method = RequestMethod.GET)
  UserInfo findUserByUsername(@RequestParam String username);

  @RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
  UserInfo findUserById(@PathVariable Long userId);
}
