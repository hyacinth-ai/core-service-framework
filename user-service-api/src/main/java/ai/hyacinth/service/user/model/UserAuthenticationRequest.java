package ai.hyacinth.service.user.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@ApiModel(value = "UserAuthenticationRequest")
@Data
@NoArgsConstructor
public class UserAuthenticationRequest {
  @ApiModelProperty(value = "username")
  @NotNull
  private String username;

  @ApiModelProperty(value = "password")
  @NotNull
  private String password;
}
