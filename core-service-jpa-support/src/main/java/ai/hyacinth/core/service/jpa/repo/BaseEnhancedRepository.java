package ai.hyacinth.core.service.jpa.repo;

import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseEnhancedRepository<T, K> {
  T lockAndRefresh(K id);
}
