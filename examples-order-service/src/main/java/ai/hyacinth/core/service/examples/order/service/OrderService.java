package ai.hyacinth.core.service.examples.order.service;

import ai.hyacinth.core.service.examples.order.dto.PlacingOrderRequest;
import ai.hyacinth.core.service.examples.order.dto.OrderInfo;
import java.util.List;

public interface OrderService {
  OrderInfo placeOrder(PlacingOrderRequest placingOrderRequest);

  List<OrderInfo> findOrdersByUserId(Long userId);
}
