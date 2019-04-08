package ai.hyacinth.core.service.web.common.payload;

import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthenticationPayload implements Serializable {
  private static final long serialVersionUID = -214526789160927487L;

  private String principalId;
  private List<String> authorities;
}
