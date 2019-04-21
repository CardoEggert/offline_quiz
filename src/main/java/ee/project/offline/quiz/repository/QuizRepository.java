package ee.project.offline.quiz.repository;

import ee.project.offline.quiz.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findById(Long id);

    @Query(value = "select case \n" +
            "\twhen ((select count(*) from quiz where is_completed = true) >  0) then true\n" +
            "\telse false\n" +
            "\tend;", nativeQuery = true)
    boolean isEnoughQuizSubmissionsForStatistics();

    @Query(value = "select * from quiz where quiz.is_completed order by (cast(quiz.result as float)/ cast(quiz.max_points  as float)) desc limit 5;", nativeQuery = true)
    List<Quiz> findTopFiveBestResultingSubmissions();
}
