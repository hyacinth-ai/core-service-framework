package ai.hyacinth.service.user.repo;

import ai.hyacinth.service.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;

public interface UserRepo extends JpaRepository<User, Long>, CustomizedUserRepo {
  @Nullable
  User findByUsername(String username);
  boolean existsByUsername(String username);
}
