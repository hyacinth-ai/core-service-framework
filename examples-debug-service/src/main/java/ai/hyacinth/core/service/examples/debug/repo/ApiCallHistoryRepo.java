package ai.hyacinth.core.service.examples.debug.repo;

import ai.hyacinth.core.service.examples.debug.domain.ApiCallHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiCallHistoryRepo extends JpaRepository<ApiCallHistory, Long> {}
