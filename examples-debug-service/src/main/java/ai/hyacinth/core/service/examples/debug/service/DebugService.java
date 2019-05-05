package ai.hyacinth.core.service.examples.debug.service;

import ai.hyacinth.core.service.examples.debug.dto.ApiCall;
import java.util.List;

public interface DebugService {
  Long recordCallHistory(ApiCall record);
  List<ApiCall> findCallHistory();
}
