package ai.hyacinth.core.service.examples.user.dto;

import ai.hyacinth.core.service.examples.user.constants.UserType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "UserInfo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo implements Serializable {

  private static final long serialVersionUID = 725395365555328566L;

  @ApiModelProperty(value = "username")
  @NotNull
  private String username;

  @NotNull private UserType userType;

  private LocalDate birthDate;

  private boolean portraitAvailable;
}
