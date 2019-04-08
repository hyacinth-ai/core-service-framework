package ai.hyacinth.service.user.repo.impl;

import ai.hyacinth.core.service.jpa.repo.BaseEnhancedRepositoryImpl;
import ai.hyacinth.service.user.domain.User;
import ai.hyacinth.service.user.repo.CustomizedUserRepo;
import org.springframework.stereotype.Repository;

@Repository
public class CustomizedUserRepoImpl extends BaseEnhancedRepositoryImpl<User, Long>
    implements CustomizedUserRepo {
  public CustomizedUserRepoImpl() {
    super(User.class);
  }
}
