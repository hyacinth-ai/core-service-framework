package ai.hyacinth.core.service.examples.user.repo.impl;

import ai.hyacinth.core.service.examples.user.domain.User;
import ai.hyacinth.core.service.examples.user.repo.CustomizedUserRepo;
import ai.hyacinth.core.service.jpa.repo.BaseEnhancedRepositoryImpl;
import org.springframework.stereotype.Repository;

@Repository
public class CustomizedUserRepoImpl extends BaseEnhancedRepositoryImpl<User, Long>
    implements CustomizedUserRepo {
  public CustomizedUserRepoImpl() {
    super(User.class);
  }
}
