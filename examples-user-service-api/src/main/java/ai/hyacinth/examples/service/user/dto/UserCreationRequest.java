package ai.hyacinth.examples.service.user.dto;

import io.swagger.annotations.ApiModel;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ApiModel(value = "UserCreationRequest")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreationRequest {
  @NotNull
  private String username;

  @NotNull
  @Size(min = 8, max = 16)
  private String password;

  private LocalDate birthDate;
}
