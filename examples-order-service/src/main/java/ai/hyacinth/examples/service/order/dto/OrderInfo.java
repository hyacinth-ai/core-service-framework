package ai.hyacinth.examples.service.order.dto;

import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderInfo {
  private Long id;
  private String serialId;
  private Long userId;
  private Long productId;
  private Integer quantity;
  private Date createdDate;
}
