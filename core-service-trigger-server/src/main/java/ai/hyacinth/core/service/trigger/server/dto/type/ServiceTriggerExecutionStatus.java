package ai.hyacinth.core.service.trigger.server.dto.type;

public enum ServiceTriggerExecutionStatus {
  WAITING,
  STARTED,
  COMPLETED,
  TIMEOUT,
  ABORTED,
  UNKNOWN,
}
