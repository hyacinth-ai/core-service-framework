package ai.hyacinth.examples.service.order.web;

import ai.hyacinth.core.service.web.common.ServiceApiConstants;
import ai.hyacinth.examples.service.order.dto.PlacingOrderRequest;
import ai.hyacinth.examples.service.order.service.OrderService;
import ai.hyacinth.examples.service.order.dto.OrderInfo;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(ServiceApiConstants.API_PREFIX)
public class OrderController {

  @Autowired private OrderService orderService;

  @PostMapping("/orders")
  public OrderInfo placeOrder(@Validated @RequestBody PlacingOrderRequest placingOrderRequest, @RequestHeader HttpHeaders httpHeaders) {
    log.info("http-headers: {}", httpHeaders);
    return orderService.placeOrder(placingOrderRequest);
  }

  @GetMapping("/orders")
  public List<OrderInfo> findOrdersByUserId(@RequestParam Long userId) {
    return orderService.findOrdersByUserId(userId);
  }
}
