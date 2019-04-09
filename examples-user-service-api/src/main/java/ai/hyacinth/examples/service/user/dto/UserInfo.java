package ai.hyacinth.examples.service.user.dto;

import ai.hyacinth.examples.service.user.constants.UserType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import org.springframework.lang.Nullable;

@ApiModel(value = "UserInfo")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserInfo implements Serializable {

  private static final long serialVersionUID = 725395365555328566L;

  @ApiModelProperty(value = "username")
  @NotNull
  private String username;

  @NotNull
  private UserType userType;

  @Nullable
  private LocalDate birthDate;

  private boolean portraitAvailable;
}
