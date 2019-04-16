package ee.project.offline.quiz.service;

import ee.project.offline.quiz.domain.Answer;
import ee.project.offline.quiz.domain.Question;
import ee.project.offline.quiz.domain.dto.add.AddQuestionDTO;
import ee.project.offline.quiz.domain.log.UserQuestionLog;
import ee.project.offline.quiz.mapper.AnswerMapper;
import ee.project.offline.quiz.mapper.QuestionMapper;
import net.bytebuddy.utility.RandomString;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class TestUtils {

    public static List<Answer> generateQuestionsWithAnswers(int amountOfQuestionsToGenerate, Long pointsPerAnswer) {
        List<Answer> answers = new ArrayList<>(amountOfQuestionsToGenerate);
        for (int indx = 0; indx < amountOfQuestionsToGenerate; indx++) {
            Question generatedQuestion = new Question(RandomString.make(10), false);
            answers.addAll(generateAnswers(4, generatedQuestion, pointsPerAnswer));
        }
        return answers;
    }

    private static List<Answer> generateAnswers(int amountOfAnswersoGenerate, Question generatedQuestion, Long pointsPerAnswer) {
        List<Answer> answers = new ArrayList<>(amountOfAnswersoGenerate);
        boolean correctForOnce = true;
        for (int indx = 0; indx < amountOfAnswersoGenerate; indx++) {
            Answer generatedAnswer  = new Answer(RandomString.make(10),  correctForOnce, pointsPerAnswer);
            correctForOnce = false;
            generatedAnswer.setQuestion(generatedQuestion);
            answers.add(generatedAnswer);
        }
        return answers;
    }

    public static void generateAndSaveQuestionsWithAnswers(int amountOfQuestionsToGenerate,
                                                           TestEntityManager testEntityManager,
                                                           Long pointsPerAnswer) {
        Stack<Question> questionStack = new Stack<>();
        List<Answer> generatedAnswersWithQuestions = generateQuestionsWithAnswers(amountOfQuestionsToGenerate, pointsPerAnswer);
        for (Answer generatedAnswer : generatedAnswersWithQuestions) {
            if (!questionStack.contains(generatedAnswer.getQuestion())) {
                Question savedQuestion = testEntityManager.persistAndFlush(generatedAnswer.getQuestion());
                questionStack.add(savedQuestion);
            }
            testEntityManager.persistAndFlush(generatedAnswer);
        }
    }

    public static List<AddQuestionDTO> generateUserQuestions(int amountOfQuestions, long pointsPerAnswer) {
        List<AddQuestionDTO> questionList = new ArrayList<>(amountOfQuestions);
        for (int indx = 0; indx < amountOfQuestions; indx++) {
            AddQuestionDTO generatedQuestion;
            List<Answer> generatedAnswers = generateAnswers(4, new Question(), pointsPerAnswer);
            generatedQuestion = QuestionMapper.fromDbToAddDto(new Question(RandomString.make(10), false),
                    generatedAnswers.stream().map(AnswerMapper::fromDbToAddDto).collect(Collectors.toList()));
            questionList.add(generatedQuestion);
        }
        return questionList;
    }

    public static void checkEachExtracted(List<?> extracted, List<?> savedObjects) {
        List<?> extractedCopy = new ArrayList<>(extracted);
        savedObjects.forEach(savedObject -> {
            assertThat(extractedCopy.contains(savedObject)).isTrue();
            extractedCopy.remove(savedObject);
        });
    }

    public static List<Answer> extractAnswersFromUserQuestions(List<AddQuestionDTO> questionsFromDTO) {
        List<Answer> answers = new ArrayList<>();
        for (AddQuestionDTO questionDTO : questionsFromDTO) {
            answers.addAll(questionDTO.getAnswers().stream()
                    .map(x ->
                            AnswerMapper.fromDtoToDb(x, QuestionMapper.fromAddDtoToDb(questionDTO)))
                    .collect(Collectors.toList()));
        }
        return answers;
    }

    public static List<Question> extractQuestionsFromUserQuestions(List<AddQuestionDTO> questionsFromDTO) {
        return questionsFromDTO.stream().map(QuestionMapper::fromAddDtoToDb).collect(Collectors.toList());
    }

    public static AddQuestionDTO generateSingleUserQuestion(long pointsPerAnswer) {
        AddQuestionDTO generatedQuestion;
        List<Answer> generatedAnswers = generateAnswers(4, new Question(), pointsPerAnswer);
        generatedQuestion = QuestionMapper.fromDbToAddDto(new Question(RandomString.make(10),  false),
                generatedAnswers.stream().map(AnswerMapper::fromDbToAddDto).collect(Collectors.toList()));
        return generatedQuestion;
    }
}
