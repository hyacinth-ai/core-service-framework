package ai.hyacinth.core.service.examples.order.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlacingOrderRequest {
  @NotNull private Long userId;

  @NotNull private Long productId;

  @NotNull
  @Min(1)
  @Max(99)
  private int quantity;
}
