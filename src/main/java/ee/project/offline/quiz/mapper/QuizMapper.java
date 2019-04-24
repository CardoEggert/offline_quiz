package ee.project.offline.quiz.mapper;

import ee.project.offline.quiz.domain.Answer;
import ee.project.offline.quiz.domain.Quiz;
import ee.project.offline.quiz.domain.dto.quiz.QuestionDTO;
import ee.project.offline.quiz.domain.dto.quiz.QuizDTO;
import ee.project.offline.quiz.domain.dto.results.QuizResults;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizMapper {
    public static QuizDTO fromDbToDto(Quiz generatedQuiz, List<QuestionDTO> answersForQuestions) {
        QuizDTO quizDTO = new QuizDTO();
        quizDTO.setId(generatedQuiz.getId());
        quizDTO.setUserName(generatedQuiz.getUserName());
        quizDTO.setUserEmail(generatedQuiz.getUserEmail());
        quizDTO.setCompleted(generatedQuiz.getCompleted());
        quizDTO.setResult(generatedQuiz.getResult());
        quizDTO.setMaxPoints(generatedQuiz.getMaxPoints());
        quizDTO.setQuestions(answersForQuestions);
        return quizDTO;
    }

    public static QuizResults fromDtoToQuizResult(QuizDTO newQuiz) {
        QuizResults qr = new QuizResults();
        qr.setQuiz(newQuiz.getId());
        qr.setQuestions(QuestionMapper.fromDtoToQuestionWrappe(newQuiz.getQuestions()));
        qr.setMaxPoints(newQuiz.getMaxPoints());
        return qr;
    }

    public static QuizResults fromQuizToQuizResults(Quiz quiz, Map<Long, List<Answer>> mapQuestionToAnswer) {
        QuizResults quizResults = new QuizResults();
        quizResults.setQuiz(quiz.getId());
        quizResults.setMaxPoints(quiz.getMaxPoints());
        quizResults.setPoints(quiz.getResult());
        quizResults.setQuestions(QuestionMapper.fromQuizQuestionsToQuestionResultWrapper(quiz.getQuestions(), mapQuestionToAnswer));
        return quizResults;
    }
}
