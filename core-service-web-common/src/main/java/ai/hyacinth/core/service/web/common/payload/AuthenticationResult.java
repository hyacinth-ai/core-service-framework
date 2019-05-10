package ai.hyacinth.core.service.web.common.payload;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthenticationResult<PrincipalType> implements Serializable {
  private static final long serialVersionUID = -214526789160927487L;
  @NotNull private List<String> authorities;
  @NotNull private PrincipalType principal;
}
