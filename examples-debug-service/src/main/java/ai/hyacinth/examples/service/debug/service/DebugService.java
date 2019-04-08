package ai.hyacinth.examples.service.debug.service;

import ai.hyacinth.examples.service.debug.dto.ApiCall;
import java.util.List;

public interface DebugService {
  Long recordCallHistory(ApiCall record);
  List<ApiCall> findCallHistory();
}
