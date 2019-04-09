package ai.hyacinth.examples.service.user.repo;

import ai.hyacinth.core.service.jpa.repo.BaseEnhancedRepository;
import ai.hyacinth.examples.service.user.domain.User;

public interface CustomizedUserRepo extends BaseEnhancedRepository<User, Long> {}
