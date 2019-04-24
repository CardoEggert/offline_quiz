package ee.project.offline.quiz.service;

import ee.project.offline.quiz.domain.Answer;
import ee.project.offline.quiz.domain.Question;
import ee.project.offline.quiz.domain.Quiz;
import ee.project.offline.quiz.domain.dto.results.QuestionResultWrapper;
import ee.project.offline.quiz.domain.dto.results.QuizAnswer;
import ee.project.offline.quiz.domain.dto.results.QuizResults;
import ee.project.offline.quiz.mapper.QuizMapper;
import ee.project.offline.quiz.repository.AnswerRepository;
import ee.project.offline.quiz.repository.QuestionRepository;
import ee.project.offline.quiz.repository.QuizRepository;
import ee.project.offline.quiz.repository.log.UserAnswerLogRepository;
import ee.project.offline.quiz.repository.log.UserQuestionLogRepository;
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

import javax.transaction.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@PropertySource("application.properties")
@DataJpaTest
@Transactional
public class QuizResultTests {


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

    private QuizService quizService;

    private Map<Long, Answer> answerMap = new HashMap<>();

    @Before
    public void setUp() {
        quizService = new QuizService(quizRepository, questionRepository,
                answerRepository, userQuestionLogRepository, userAnswerLogRepository);
        answerMap = answerRepository.findAll().stream().collect(Collectors.toMap(Answer::getId, y -> y));
    }

    @After
    public void tearDown() {
        questionRepository.deleteAll();
        answerRepository.deleteAll();
        testEntityManager.clear();
    }

    @Sql(scripts = {"/scripts/clear_db.sql", "/scripts/all_pos_questions.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    public void pickAllRightAnswersFromAllPositiveQuestions() {
        Quiz quiz = quizService.getNewQuiz();
        QuizResults quizResults = setUpQuizResults(quiz);
        pickAllRightAnwers(quizResults);

        QuizResults validatedResults = quizService.validateResults(quizResults);

        assertThat(validatedResults.getPoints()).isEqualTo(validatedResults.getMaxPoints());
    }

    @Sql(scripts = {"/scripts/clear_db.sql", "/scripts/all_pos_questions.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    public void pickAllWrongAnswersFromAllPositiveQuestions() {
        Quiz quiz = quizService.getNewQuiz();
        QuizResults quizResults = setUpQuizResults(quiz);
        pickAllWrongAnwers(quizResults);

        QuizResults validatedResults = quizService.validateResults(quizResults);

        assertThat(validatedResults.getPoints()).isEqualTo(0L);
    }

    @Sql(scripts = {"/scripts/clear_db.sql", "/scripts/all_neg_questions.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    public void pickAllRightAnswersFromAllNegativeQuestions() {
        Quiz quiz = quizService.getNewQuiz();
        QuizResults quizResults = setUpQuizResults(quiz);
        pickAllRightAnwers(quizResults);

        QuizResults validatedResults = quizService.validateResults(quizResults);

        assertThat(validatedResults.getPoints()).isEqualTo(validatedResults.getMaxPoints());
    }

    @Sql(scripts = {"/scripts/clear_db.sql", "/scripts/all_neg_questions.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    public void pickAllWrongAnswersFromAllNegativeQuestions() {
        Quiz quiz = quizService.getNewQuiz();
        QuizResults quizResults = setUpQuizResults(quiz);
        pickAllWrongAnwers(quizResults);

        QuizResults validatedResults = quizService.validateResults(quizResults);

        assertThat(validatedResults.getPoints()).isEqualTo(0L);
    }

    @Sql(scripts = {"/scripts/clear_db.sql", "/scripts/half_pos_questions_and_half_negative_questions.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    public void pickAllRightAnswersFromHalfNegandHalfPosQuestions() {
        Quiz quiz = quizService.getNewQuiz();
        QuizResults quizResults = setUpQuizResults(quiz);
        pickAllRightAnwers(quizResults);

        QuizResults validatedResults = quizService.validateResults(quizResults);

        assertThat(validatedResults.getPoints()).isEqualTo(validatedResults.getMaxPoints());
    }

    @Sql(scripts = {"/scripts/clear_db.sql", "/scripts/half_pos_questions_and_half_negative_questions.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    public void pickAllWrongAnswersFromHalfNegandHalfPosQuestions() {
        Quiz quiz = quizService.getNewQuiz();
        QuizResults quizResults = setUpQuizResults(quiz);
        pickAllWrongAnwers(quizResults);

        QuizResults validatedResults = quizService.validateResults(quizResults);

        assertThat(validatedResults.getPoints()).isEqualTo(0L);
    }

    @Sql(scripts = {"/scripts/clear_db.sql", "/scripts/half_pos_questions_and_half_negative_questions.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    public void pickEvenlySoSumWouldBeZeroFromHalfNegandHalfPosQuestions() {
        Quiz quiz = quizService.getNewQuiz();
        QuizResults quizResults = setUpQuizResults(quiz);
        pickEvenlyAnswersSoSumWouldBeZero(quizResults);

        QuizResults validatedResults = quizService.validateResults(quizResults);

        assertThat(validatedResults.getPoints()).isEqualTo(0L);
    }

    @Sql(scripts = {"/scripts/clear_db.sql", "/scripts/half_pos_questions_and_half_negative_questions.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    public void pickAllRightAnswersForPositiveQuestionsAndWrongAnswersForNegativeQuestionsFromHalfNegandHalfPosQuestions() {
        Quiz quiz = quizService.getNewQuiz();
        QuizResults quizResults = setUpQuizResults(quiz);
        pickAllPositiveAnswersCorrectAndAllNegativeAnswersWrong(quizResults);

        QuizResults validatedResults = quizService.validateResults(quizResults);

        assertThat(validatedResults.getPoints()).isEqualTo(5L);
    }

    private void pickAllPositiveAnswersCorrectAndAllNegativeAnswersWrong(QuizResults quizResults) {
        for (QuestionResultWrapper qrw : quizResults.getQuestions()) {
            for (QuizAnswer qa : qrw.getAnswers()) {
                Answer answer = answerMap.get(qa.getAnswer());
                if (answer.getDescription().contains("pos")) {
                    qa.setAnswered(answer.isCorrectAnswer());
                } else {
                    qa.setAnswered(!answer.isCorrectAnswer());
                }
            }
        }
    }

    private void pickEvenlyAnswersSoSumWouldBeZero(QuizResults quizResults) {
        for (QuestionResultWrapper qrw : quizResults.getQuestions()) {
            for (QuizAnswer qa : qrw.getAnswers()) {
                if (answerMap.get(qa.getAnswer()).getDescription().contains("pos")) {
                    qa.setAnswered(false);
                } else {
                    qa.setAnswered(false);
                    if (!answerMap.get(qa.getAnswer()).isCorrectAnswer()) {
                        qa.setAnswered(true);
                    }
                }
            }
        }
    }

    private void pickAllWrongAnwers(QuizResults quizResults) {
        for (QuestionResultWrapper qrw : quizResults.getQuestions()) {
            for (QuizAnswer qa : qrw.getAnswers()) {
                qa.setAnswered(!answerMap.get(qa.getAnswer()).isCorrectAnswer());
            }
        }
    }

    private void pickAllRightAnwers(QuizResults quizResults) {
        for (QuestionResultWrapper qrw : quizResults.getQuestions()) {
            for (QuizAnswer qa : qrw.getAnswers()) {
                qa.setAnswered(answerMap.get(qa.getAnswer()).isCorrectAnswer());
            }
        }
    }

    private QuizResults setUpQuizResults(Quiz quiz) {
        Map<Long, List<Answer>> mapQuestionToAnswer  = mapAnswersToQuestions(quiz.getQuestions());
        QuizResults quizResults = QuizMapper.fromQuizToQuizResults(quiz, mapQuestionToAnswer);
        return quizResults;
    }

    private Map<Long, List<Answer>> mapAnswersToQuestions(Set<Question> questions) {
        Map<Long, List<Answer>> mappedAnswersToQuestions = new HashMap<>(questions.size());
        for (Question question : questions) {
            mappedAnswersToQuestions.put(question.getId(), answerRepository.findAllByQuestionId(question.getId()));
        }
        return mappedAnswersToQuestions;
    }
}
