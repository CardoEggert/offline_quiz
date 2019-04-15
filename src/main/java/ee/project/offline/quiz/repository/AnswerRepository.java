package ee.project.offline.quiz.repository;

import ee.project.offline.quiz.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findAll();

    @Query(value = "select a.* from Answer a where a.question_id = ?1", nativeQuery = true)
    List<Answer> findAllByQuestionId(Long questionId);
}
