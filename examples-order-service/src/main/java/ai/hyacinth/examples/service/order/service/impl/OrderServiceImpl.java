package ai.hyacinth.examples.service.order.service.impl;

import ai.hyacinth.core.service.web.common.ServiceApiException;
import ai.hyacinth.examples.service.order.domain.Order;
import ai.hyacinth.examples.service.order.dto.PlacingOrderRequest;
import ai.hyacinth.examples.service.order.service.OrderService;
import ai.hyacinth.examples.service.order.dto.OrderInfo;
import ai.hyacinth.examples.service.order.repo.OrderRepo;
import ai.hyacinth.examples.service.user.api.UserApi;
import ai.hyacinth.examples.service.user.constants.UserType;
import ai.hyacinth.examples.service.user.dto.UserInfo;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {
  @Autowired private OrderRepo orderRepo;
  @Autowired private UserApi userApi;

  @Override
  public OrderInfo placeOrder(PlacingOrderRequest placingOrderRequest) {
    UserInfo userInfo = userApi.findUserById(placingOrderRequest.getUserId());
    if (userInfo == null) {
      throw new ServiceApiException(OrderServiceErrorCode.NO_SUCH_USER);
    }
    if (!userInfo.getUserType().equals(UserType.PUBLIC)) {
      throw new ServiceApiException(OrderServiceErrorCode.PUBLIC_USER_ONLY);
    }
    Order order = new Order();
    order.setUserId(placingOrderRequest.getUserId());
    order.setProductId(placingOrderRequest.getProductId());
    order.setQuantity(placingOrderRequest.getQuantity());
    order.setSerialId(UUID.randomUUID().toString());
    orderRepo.save(order);
    return toOrderInfo(order);
  }

  private OrderInfo toOrderInfo(Order order) {
    OrderInfo info = new OrderInfo();
    info.setId(order.getId());
    info.setSerialId(order.getSerialId());
    info.setProductId(order.getProductId());
    info.setQuantity(order.getQuantity());
    info.setUserId(order.getUserId());
    info.setCreatedDate(order.getCreatedDate());
    return info;
  }

  @Override
  public List<OrderInfo> findOrdersByUserId(Long userId) {
    return orderRepo.findByUserId(userId).stream()
        .map(this::toOrderInfo)
        .collect(Collectors.toList());
  }
}
