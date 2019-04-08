package ai.hyacinth.service.user;

import ai.hyacinth.service.user.api.UserApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackageClasses = {UserApi.class})
public class UserApiConfiguration {}
