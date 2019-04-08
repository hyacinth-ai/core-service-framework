package ai.hyacinth.examples.service.debug.service.impl;

import ai.hyacinth.examples.service.debug.domain.ApiCallHistory;
import ai.hyacinth.examples.service.debug.dto.ApiCall;
import ai.hyacinth.examples.service.debug.repo.ApiCallHistoryRepo;
import ai.hyacinth.examples.service.debug.service.DebugService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
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
