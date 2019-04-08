package ai.hyacinth.examples.service.order.service;

import ai.hyacinth.examples.service.order.dto.PlacingOrderRequest;
import ai.hyacinth.examples.service.order.dto.OrderInfo;
import java.util.List;

public interface OrderService {
  OrderInfo placeOrder(PlacingOrderRequest placingOrderRequest);

  List<OrderInfo> findOrdersByUserId(Long userId);
}
