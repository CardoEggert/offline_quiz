package ee.project.offline.quiz.service;

import ee.project.offline.quiz.domain.Answer;
import ee.project.offline.quiz.domain.Question;
import ee.project.offline.quiz.domain.Quiz;
import ee.project.offline.quiz.domain.dto.AnswerDTO;
import ee.project.offline.quiz.domain.dto.QuestionDTO;
import ee.project.offline.quiz.domain.dto.QuizDTO;
import ee.project.offline.quiz.domain.dto.add.AddAnswerDTO;
import ee.project.offline.quiz.domain.dto.add.AddQuestionDTO;
import ee.project.offline.quiz.domain.dto.results.QuestionResultWrapper;
import ee.project.offline.quiz.domain.dto.results.QuizAnswer;
import ee.project.offline.quiz.domain.dto.results.QuizResults;
import ee.project.offline.quiz.domain.log.UserAnswerLog;
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
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ee.project.offline.quiz.service.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@PropertySource("application.properties")
@DataJpaTest
@Transactional
public class QuizServiceTests {

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

    @Before
    public void setUp() {
        quizService = new QuizService(quizRepository, questionRepository,
                answerRepository, userQuestionLogRepository, userAnswerLogRepository);
    }

    @After
    public void tearDown() {
        questionRepository.deleteAll();
        answerRepository.deleteAll();
        testEntityManager.clear();
    }

    @Test
    public void generateNewQuiz() {
        generateAndSaveQuestionsWithAnswers(100, testEntityManager);
        Quiz quiz = quizService.getNewQuiz();
        assertThat(quiz.getQuestions().size()).isEqualTo(10);
    }

    @Test(expected = NotEnoughQuestionsException.class)
    public void getNewQuizWhenNotEnoughQuestions() {
        Quiz notEnoughQuestionsQuiz = quizService.getNewQuiz();
    }

    @Test
    public void addQuestionsToDatabase() {
        List<AddQuestionDTO> userQuestions = generateUserQuestions(20);

        List<Question> extractedQuestions = extractQuestionsFromUserQuestions(userQuestions);
        List<Answer> extractedAnswers = extractAnswersFromUserQuestions(userQuestions);

        userQuestions.forEach(quizService::saveQuestion);

        assertThat(questionRepository.findAll().size()).isEqualTo(extractedQuestions.size());
        assertThat(answerRepository.findAll().size()).isEqualTo(extractedAnswers.size());
        checkEachExtracted(extractQuestionDescription(extractedQuestions), extractQuestionDescription(new ArrayList<>(questionRepository.findAll())));
        checkEachExtracted(extractAnswerDescription(extractedAnswers), extractAnswerDescription(new ArrayList<>(answerRepository.findAll())));
    }

    @Test(expected = InvalidQuestionException.class)
    public void addMultipleChoiceQuestionWithNoRightAnswersToDatabase() {
        AddQuestionDTO userQuestions = TestUtils.generateSingleUserQuestion();
        userQuestions.setMultipleChoice(true);
        setAllAnswersToFalse(userQuestions.getAnswers());

        quizService.saveQuestion(userQuestions);
    }

    private void setAllAnswersToFalse(List<AddAnswerDTO> answers) {
        for (AddAnswerDTO answer : answers) {
            answer.setCorrectAnswer(false);
        }
    }

    @Repeat(value = 20)
    @Test
    public void validateResults() {
        QuizResults generatedQuizResults = createQuizResults();
        QuizResults validatedResults = quizService.validateResults(generatedQuizResults);
        assertThat(validatedResults.getPoints() >= 0 && validatedResults.getPoints() <= validatedResults.getMaxPoints()).isTrue();
    }


    @Test
    public void validateResultsIfAllMultipleQuestions() {
        QuizResults generatedQuizResults = createQuizResults();

        setupMultipleQuestion(generatedQuizResults,0, 10);

        QuizResults validatedResults = quizService.validateResults(generatedQuizResults);

        assertThat(validatedResults.getPoints()).isEqualTo(200L);
    }

    @Test
    public void validateResultsIfAllSingleAnswerQuestions() {
        QuizResults generatedQuizResults = createQuizResults();

        setupSingleQuestion(generatedQuizResults, 0, 10);

        QuizResults validatedResults = quizService.validateResults(generatedQuizResults);

        assertThat(validatedResults.getPoints() >= 0 && validatedResults.getPoints() <= validatedResults.getMaxPoints()).isTrue();
        assertThat(validatedResults.getPoints()).isEqualTo(0L);
    }

    @Test
    public void checkLogs() {
        QuizResults generatedQuizResults = createQuizResults();

        QuizResults validatedResults = quizService.validateResults(generatedQuizResults);

        assertThat(userQuestionLogRepository.findAll().size() > 0).isTrue();
        assertThat(userAnswerLogRepository.findAll().size() > 0).isTrue();
    }

    private void setupSingleQuestion(QuizResults generatedQuizResults, int startingIndex, int endingIndex) {
        List<QuestionResultWrapper> questions = generatedQuizResults.getQuestions();
        for (int indx = startingIndex; indx < endingIndex; indx++) {
            QuestionResultWrapper question = questions.get(indx);
            for (QuizAnswer answer : question.getAnswers()) {
                answer.setAnswered(false);
                Answer dbAnswer = testEntityManager.find(Answer.class, answer.getAnswer());
                dbAnswer.setPoints(5L);
                testEntityManager.persistAndFlush(dbAnswer);
            }
        }
    }

    @Test
    public void validateResultsIfHalfSingleAndHalfMultipleAnswerQuestions() {
        QuizResults generatedQuizResults = createQuizResults();

        setupMultipleQuestion(generatedQuizResults,0, 5);
        setupSingleQuestion(generatedQuizResults, 5, 10);

        QuizResults validatedResults = quizService.validateResults(generatedQuizResults);

        assertThat(validatedResults.getPoints() >= 0 && validatedResults.getPoints() <= validatedResults.getMaxPoints()).isTrue();
        assertThat(validatedResults.getPoints()).isEqualTo(100L);
    }

    @Test
    public void validateResultsForSingleAnswerQuestionsWithOneTrueValue() {
        QuizResults generatedQuizResults = createQuizResults();

        setupSingleQuestion(generatedQuizResults, 0, 10);

        for(QuestionResultWrapper question : generatedQuizResults.getQuestions()) {
            question.getAnswers().get(2).setAnswered(true);
        }

        QuizResults validatedResults = quizService.validateResults(generatedQuizResults);

        for(QuestionResultWrapper question : generatedQuizResults.getQuestions()) {
            for (QuizAnswer answer : question.getAnswers()) {
                assertThat(answer.getAnswered()).isFalse();
            }
        }
        assertThat(validatedResults.getPoints() >= 0 && validatedResults.getPoints() <= validatedResults.getMaxPoints()).isTrue();
        assertThat(validatedResults.getPoints()).isEqualTo(0L);
    }


    private void setupMultipleQuestion(QuizResults generatedQuizResults, int startingIndex, int endingIndex) {
        List<QuestionResultWrapper> questions = generatedQuizResults.getQuestions();
        for (int i = startingIndex; i < endingIndex; i++) {
            QuestionResultWrapper question = questions.get(i);
            question.setMultipleChoice(true);
            Question dbQuestion = testEntityManager.find(Question.class, question.getQuestion());
            dbQuestion.setMultipleChoice(true);
            for (QuizAnswer answer : question.getAnswers()) {
                answer.setAnswered(true);
                Answer dbAnswer = testEntityManager.find(Answer.class, answer.getAnswer());
                dbAnswer.setPoints(5L);
                dbAnswer.setCorrectAnswer(true);
                testEntityManager.persistAndFlush(dbAnswer);
            }
        }
        Quiz dbQuiz = testEntityManager.find(Quiz.class, generatedQuizResults.getQuiz());
        generatedQuizResults.setMaxPoints((endingIndex-startingIndex) * 20L);
    }

    private QuizResults createQuizResults() {
        generateAndSaveQuestionsWithAnswers(20, testEntityManager);
        Quiz newQuiz = quizService.getNewQuiz();
        return QuizMapper.fromDtoToQuizResult(quizService.convertQuiz(newQuiz));
    }

    @Test(expected = InvalidQuestionException.class)
    public void addSingleChoiceQuestionWithNoRightAnswersToDatabase() {
        AddQuestionDTO userQuestions = TestUtils.generateSingleUserQuestion();
        userQuestions.setMultipleChoice(false);
        setAllAnswersToFalse(userQuestions.getAnswers());

        quizService.saveQuestion(userQuestions);
    }

    @Test(expected = InvalidQuestionException.class)
    public void addQuestionWithNoAnswer() {
        AddQuestionDTO userQuestions = TestUtils.generateSingleUserQuestion();
        userQuestions.setMultipleChoice(false);
        userQuestions.setAnswers(new ArrayList<>());

        quizService.saveQuestion(userQuestions);
    }

    @Test
    public void addAnswersToQuestion() {
        generateAndSaveQuestionsWithAnswers(100, testEntityManager);
        Quiz quiz = quizService.getNewQuiz();
        QuizDTO quizDTO = quizService.convertQuiz(quiz);
        assertThat(quizDTO.getQuestions().size() > 0).isEqualTo(true);
    }

    @Test
    public void calculateMaxPoints() {
        generateAndSaveQuestionsWithAnswers(100, testEntityManager);
        Quiz quiz = quizService.getNewQuiz();
        List<Long> questionIds = quiz.getQuestions().stream().map(Question::getId).collect(Collectors.toList());
        long pointSum = 0;
        for (Long questionId : questionIds) {
            List<Answer> answersToQuestion = answerRepository.findAllByQuestionId(questionId);
            for (Answer answer : answersToQuestion) {
                pointSum += answer.getPoints();
            }
        }
        assertThat(pointSum).isEqualTo(quiz.getMaxPoints());
    }

    @Repeat(value = 10)
    @Test
    public void quizQuestionContainNoDuplicates() {
        generateAndSaveQuestionsWithAnswers(15, testEntityManager);
        Quiz quiz = quizService.getNewQuiz();
        assertNoDuplicates(quiz.getQuestions());
    }

    private void assertNoDuplicates(Set<Question> questions) {
        int nrOfQuestions = questions.size();
        assertThat(questions.stream().map(Question::getId).collect(Collectors.toSet()).size())
                .isEqualTo(nrOfQuestions);
        assertNoQuestionMatches(questions);
    }

    private void assertNoQuestionMatches(Set<Question> questions) {
        List<Long> questionCheckList = new ArrayList<>(questions.size());
        for (Question question : questions) {
            assertThat(questionCheckList.contains(question.getId())).isFalse();
            questionCheckList.add(question.getId());
        }
    }

    private List<String> extractAnswerDescription(List<Answer> extractedAnswers) {
        return extractedAnswers.stream().map(Answer::getDescription).collect(Collectors.toList());
    }

    private List<String> extractQuestionDescription(List<Question> extractedQuestions) {
        return extractedQuestions.stream().map(Question::getDescription).collect(Collectors.toList());
    }

}
