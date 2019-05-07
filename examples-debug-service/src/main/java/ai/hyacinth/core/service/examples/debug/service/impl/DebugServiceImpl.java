package ai.hyacinth.core.service.examples.debug.service.impl;

import ai.hyacinth.core.service.examples.debug.repo.ApiCallHistoryRepo;
import ai.hyacinth.core.service.examples.debug.domain.ApiCallHistory;
import ai.hyacinth.core.service.examples.debug.dto.ApiCall;
import ai.hyacinth.core.service.examples.debug.service.DebugService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class DebugServiceImpl implements DebugService {
  @Autowired private ApiCallHistoryRepo historyRepo;

  @Override
  public Long recordCallHistory(ApiCall callRecord) {
    ApiCallHistory history = ApiCallHistory.builder()
        .path(callRecord.getPath())
        .requestMethod(callRecord.getRequestMethod())
        .requestTime(callRecord.getRequestTime())
        .requestHeaders(callRecord.getRequestHeaders())
        .requestParameters(callRecord.getRequestParameters())
        .requestBody(callRecord.getRequestBody())
        .build();
    historyRepo.save(history);
    return history.getId();
  }

  @Override
  @Cacheable("callHistory")
  public List<ApiCall> findCallHistory() {
    return historyRepo.findAll().stream().map(this::toApiCall).collect(Collectors.toList());
  }

  private ApiCall toApiCall(ApiCallHistory callRecord) {
    return ApiCall.builder()
        .path(callRecord.getPath())
        .requestMethod(callRecord.getRequestMethod())
        .requestTime(callRecord.getRequestTime())
        .requestHeaders(callRecord.getRequestHeaders())
        .requestParameters(callRecord.getRequestParameters())
        .requestBody(callRecord.getRequestBody())
        .build();
  }
}
