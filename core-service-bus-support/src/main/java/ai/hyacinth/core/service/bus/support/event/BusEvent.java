package ai.hyacinth.core.service.bus.support.event;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.cloud.bus.event.RemoteApplicationEvent;
import org.springframework.lang.Nullable;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonPropertyOrder({"payloadType", "payload"})
public class BusEvent<PayloadClass> extends RemoteApplicationEvent {
  private String eventType;
  private PayloadClass payload;
  private String payloadType; // optional, a indication for payload format or version

  public BusEvent(
      Object source,
      String originService,
      @Nullable String destinationService,
      String eventType,
      @Nullable PayloadClass payload,
      @Nullable String payloadType) {
    super(source, originService, destinationService);
    this.eventType = eventType;
    this.payload = payload;
    this.payloadType = payloadType;
  }

  public BusEvent(Object source, String originService, String destinationService) {
    super(source, originService, destinationService);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("BusEvent{");
    sb.append("eventType='").append(eventType).append('\'');
    sb.append(", payload=").append(payload);
    sb.append(", payloadType='").append(payloadType).append('\'');
    sb.append(", timestamp=").append(getTimestamp());
    sb.append(", id='").append(getId()).append('\'');
    sb.append(", originService='").append(getOriginService()).append('\'');
    sb.append(", destinationService='").append(getDestinationService()).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
