package ai.hyacinth.examples.service.debug.repo;

import ai.hyacinth.examples.service.debug.domain.ApiCallHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiCallHistoryRepo extends JpaRepository<ApiCallHistory, Long> {}
