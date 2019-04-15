package ee.project.offline.quiz.mapper;

import ee.project.offline.quiz.domain.Quiz;
import ee.project.offline.quiz.domain.dto.QuestionDTO;
import ee.project.offline.quiz.domain.dto.QuizDTO;
import ee.project.offline.quiz.domain.dto.results.QuizResults;

import java.util.List;

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
}
