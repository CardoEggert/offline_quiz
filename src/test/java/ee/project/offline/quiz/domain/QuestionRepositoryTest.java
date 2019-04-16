package ee.project.offline.quiz.domain;

import ee.project.offline.quiz.repository.QuestionRepository;
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
import java.util.List;
import java.util.Stack;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@PropertySource("application.properties")
@Transactional
public class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    private static TestUtils testUtils;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void findAllQuestions() {
        List<Answer> generatedQuestionsWithAnswers = TestUtils.generateQuestionsWithAnswers(15, 5L);

        Stack<Question> savedQuestions = saveQuestionsAndAnswers(generatedQuestionsWithAnswers);
        List<Question> allQuestions = this.questionRepository.findAll();

        assertThat(savedQuestions.size()).isEqualTo(allQuestions.size());
        doQuestionsMatch(savedQuestions, allQuestions);

    }

    private void doQuestionsMatch(Stack<Question> savedQuestions, List<Question> allQuestions) {
        List<Question> savedQuestionArray = new ArrayList<>(savedQuestions);
        for (Question question : allQuestions) {
            assertThat(savedQuestionArray.contains(question)).isTrue();
            savedQuestionArray.remove(question);
        }
        assertThat(savedQuestionArray.isEmpty()).isTrue();
    }

    private Stack<Question> saveQuestionsAndAnswers(List<Answer> generatedQuestionsWithAnswers) {
        Stack<Question> questions = new Stack<>();
        for (Answer generatedAnswer : generatedQuestionsWithAnswers) {
            if (!questions.contains(generatedAnswer.getQuestion())) {
                Question savedQuesion = testEntityManager.persistAndFlush(generatedAnswer.getQuestion());
                questions.add(savedQuesion);
            }
            testEntityManager.persistAndFlush(generatedAnswer);
        }
        return questions;
    }
}
