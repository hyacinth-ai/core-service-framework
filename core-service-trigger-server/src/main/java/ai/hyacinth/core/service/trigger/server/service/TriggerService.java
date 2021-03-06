package ai.hyacinth.core.service.trigger.server.service;

import ai.hyacinth.core.service.trigger.server.dto.TriggerChangeResult;
import ai.hyacinth.core.service.trigger.server.dto.TriggerCreationRequest;
import ai.hyacinth.core.service.trigger.server.dto.TriggerInfo;
import ai.hyacinth.core.service.trigger.server.dto.TriggerQueryRequest;
import ai.hyacinth.core.service.trigger.server.dto.TriggerUpdateRequest;
import java.util.List;
import org.springframework.lang.NonNull;

public interface TriggerService {
  @NonNull
  TriggerChangeResult removeTrigger(Long triggerId);

  @NonNull
  TriggerInfo createTrigger(TriggerCreationRequest createRequest);

  TriggerInfo findTriggerById(Long id);

  TriggerInfo findTriggerByServiceAndName(String service, String name);

  @NonNull
  List<TriggerInfo> findAllTriggers(TriggerQueryRequest queryRequest);

  @NonNull
  TriggerInfo updateTrigger(Long id, TriggerUpdateRequest updateRequest);
}
