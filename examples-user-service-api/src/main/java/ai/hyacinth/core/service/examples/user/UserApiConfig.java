package ai.hyacinth.core.service.examples.user;

import ai.hyacinth.core.service.examples.user.api.UserApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackageClasses = {UserApi.class})
public class UserApiConfig {}
