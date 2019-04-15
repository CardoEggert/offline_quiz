package ee.project.offline.quiz.repository.log;

import ee.project.offline.quiz.domain.log.UserQuestionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserQuestionLogRepository extends JpaRepository<UserQuestionLog, Long> {
}
