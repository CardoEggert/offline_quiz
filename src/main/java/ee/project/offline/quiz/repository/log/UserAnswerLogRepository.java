package ee.project.offline.quiz.repository.log;

import ee.project.offline.quiz.domain.log.UserAnswerLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAnswerLogRepository extends JpaRepository<UserAnswerLog, Long> {
}
