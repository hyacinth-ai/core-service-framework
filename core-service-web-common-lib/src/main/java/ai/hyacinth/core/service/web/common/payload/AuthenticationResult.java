package ai.hyacinth.core.service.web.common.payload;

import java.io.Serializable;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthenticationResult<PrincipalType> implements Serializable {
  private static final long serialVersionUID = -214526789160927487L;

  private List<String> authorities;
  private PrincipalType principal;
}
