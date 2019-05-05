package ai.hyacinth.core.service.examples.user.repo;

import ai.hyacinth.core.service.examples.user.domain.User;
import ai.hyacinth.core.service.jpa.repo.BaseEnhancedRepository;

public interface CustomizedUserRepo extends BaseEnhancedRepository<User, Long> {}
