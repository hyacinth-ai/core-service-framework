package ai.hyacinth.core.service.examples.order.repo;

import ai.hyacinth.core.service.examples.order.domain.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Order, Long> {
  List<Order> findByUserId(Long userId);
}
