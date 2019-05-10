package ai.hyacinth.core.service.examples.user.repo;

import ai.hyacinth.core.service.examples.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long>, CustomizedUserRepo {
  User findByUsername(String username);

  boolean existsByUsername(String username);
}
