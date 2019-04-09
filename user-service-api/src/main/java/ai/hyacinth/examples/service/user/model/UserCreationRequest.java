package ai.hyacinth.examples.service.user.model;

import io.swagger.annotations.ApiModel;
import java.time.LocalDate;
import java.util.Date;
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
  @NotNull String name;

  @NotNull
  @Size(min = 8, max = 16)
  String password;

  LocalDate birthDate;
}
