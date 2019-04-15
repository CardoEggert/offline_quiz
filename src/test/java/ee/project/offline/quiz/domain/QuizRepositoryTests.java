package ee.project.offline.quiz.domain;

import ee.project.offline.quiz.repository.QuizRepository;
import ee.project.offline.quiz.service.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@PropertySource("application.properties")
public class QuizRepositoryTests {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void findQuizById() {
        Quiz savedQuiz = testEntityManager.persistFlushFind(new Quiz());
        Quiz foundQuiz = this.quizRepository.findById(savedQuiz.getId()).get();
        assertThat(savedQuiz.getId()).isEqualTo(foundQuiz.getId());
    }

    @Transactional
    @Test
    public void createQuizWithUser() {
        List<Answer> questionsWithAnswers = TestUtils.generateQuestionsWithAnswers(10);
        List<Question> extractedQuestions = extractQuestions(questionsWithAnswers);
        Quiz generateQuiz = new Quiz(new HashSet<>(extractedQuestions));
        generateQuiz.setUserName("Eesnimi Perekonnanimi");
        generateQuiz.setUserEmail("eesnimi.perekonnanimi@gmail.com");
        Quiz createdQuiz = this.quizRepository.save(generateQuiz);
        Quiz quiz = this.quizRepository.findById(createdQuiz.getId()).get();

        assertThat(quiz.getQuestions().size()).isEqualTo(10);
        assertThat(quiz.getId()).isEqualTo(createdQuiz.getId());
        assertThat(quiz.getUserName()).isEqualTo("Eesnimi Perekonnanimi");
        assertThat(quiz.getUserEmail()).isEqualTo("eesnimi.perekonnanimi@gmail.com");
    }

    private List<Question> extractQuestions(List<Answer> questionsWithAnswers) {
        Stack<Question> extractableQuestions = new Stack<>();
        for (Answer answer : questionsWithAnswers) {
            if (!extractableQuestions.contains(answer.getQuestion())) {
                testEntityManager.persistAndFlush(answer.getQuestion());
                extractableQuestions.add(answer.getQuestion());
            }
            testEntityManager.persistAndFlush(answer);
        }
        return new ArrayList<>(extractableQuestions);
    }
}
