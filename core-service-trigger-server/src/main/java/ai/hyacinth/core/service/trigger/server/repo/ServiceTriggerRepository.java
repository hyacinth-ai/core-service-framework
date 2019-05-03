package ai.hyacinth.core.service.trigger.server.repo;

import ai.hyacinth.core.service.trigger.server.domain.ServiceTrigger;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceTriggerRepository extends JpaRepository<ServiceTrigger, Long> {
  Optional<ServiceTrigger> findByServiceAndName(String service, String name);
}
