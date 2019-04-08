package ai.hyacinth.service.user.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ApiModel(value = "UserInfo")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserInfo implements Serializable {

  private static final long serialVersionUID = 725395365555328566L;

  @ApiModelProperty(value = "username")
  @NotNull
  private String username;
}
