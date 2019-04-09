package ai.hyacinth.examples.service.user.repo;

import ai.hyacinth.examples.service.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;

public interface UserRepo extends JpaRepository<User, Long>, CustomizedUserRepo {
  @Nullable
  User findByUsername(String username);
  boolean existsByUsername(String username);
}
