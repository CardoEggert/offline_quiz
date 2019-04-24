package ee.project.offline.quiz.service;

import ee.project.offline.quiz.domain.Quiz;
import ee.project.offline.quiz.domain.statistics.StatisticItem;
import ee.project.offline.quiz.domain.statistics.StatisticsList;
import ee.project.offline.quiz.repository.AnswerRepository;
import ee.project.offline.quiz.repository.QuestionRepository;
import ee.project.offline.quiz.repository.QuizRepository;
import ee.project.offline.quiz.repository.log.UserAnswerLogRepository;
import ee.project.offline.quiz.repository.log.UserQuestionLogRepository;
import ee.project.offline.quiz.service.exceptions.NotEnoughQuestionsException;
import net.bytebuddy.utility.RandomString;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@PropertySource("application.properties")
@DataJpaTest
@Transactional
public class StatisticTests {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private UserQuestionLogRepository userQuestionLogRepository;

    @Autowired
    private UserAnswerLogRepository userAnswerLogRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private StatisticsService statisticsService;

    @Before
    public void setUp() {
        statisticsService = new StatisticsService(quizRepository);
    }

    @After
    public void tearDown() {
        questionRepository.deleteAll();
        answerRepository.deleteAll();
        testEntityManager.clear();
    }

    @Sql(scripts = {"/scripts/clear_db.sql", "/scripts/full_test_db.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    public void checkStatistics() {
        StatisticsList overallStatistics = statisticsService.getStatistics();
        assertThat(overallStatistics.getStatisticItems().size()).isEqualTo(5);
        StatisticsList dbItems = getQuizSubmissions();

        for (int i = 0; i < 5; i++) {
            double gottenStatistics = overallStatistics.getStatisticItems().get(0).getPercentageResult();
            double dbStatistics = dbItems.getStatisticItems().get(0).getPercentageResult();
            assertThat(gottenStatistics).isEqualTo(dbStatistics);
        }
    }

    private StatisticsList getQuizSubmissions() {
        List<Quiz> allQuizSubmissions = quizRepository.findAll().stream()
                .filter(Quiz::getCompleted).sorted(Comparator.comparing(x -> x.getResult() / x.getMaxPoints()))
                .collect(Collectors.toList());
        List<StatisticItem> statisticItems = new ArrayList<>(5);
        for (Quiz quizSubmission : allQuizSubmissions) {
            statisticItems.add(new StatisticItem(RandomString.make(15), calculatePercentage(quizSubmission)));
        }
        statisticItems = statisticItems.stream().sorted((f1, f2) -> Double.compare(f2.getPercentageResult(), f1.getPercentageResult())).collect(Collectors.toList());
        return new StatisticsList(statisticItems);
    }

    private double calculatePercentage(Quiz quizSubmission) {
        if (quizSubmission.getResult() != null && quizSubmission.getMaxPoints() != null) {
            return (double) quizSubmission.getResult() / quizSubmission.getMaxPoints();
        }
        return 0.0;
    }

    @Sql(scripts = {"/scripts/clear_db.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test(expected = NotEnoughQuestionsException.class)
    public void checkStatisticsWithNoQuizSubmissions() {
        StatisticsList overallStatistics = statisticsService.getStatistics();
    }

    @Sql(scripts = {"/scripts/clear_db.sql", "/scripts/full_test_db.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    public void checkEnoughQuestionsForStatistics() {
        assertThat(statisticsService.isEnoughtQuizSubmissions()).isTrue();
    }

    @Sql(scripts = {"/scripts/clear_db.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    public void checkEnoughQuestionsForStatisticsIfNoQuizSubmissions() {
        assertThat(statisticsService.isEnoughtQuizSubmissions()).isFalse();
    }


}