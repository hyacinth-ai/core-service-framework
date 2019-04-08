package ai.hyacinth.examples.service.order.repo;

import ai.hyacinth.examples.service.order.domain.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Order, Long> {
  List<Order> findByUserId(Long userId);
}
