package ee.project.offline.quiz.mapper;

import ee.project.offline.quiz.domain.Answer;
import ee.project.offline.quiz.domain.Question;
import ee.project.offline.quiz.domain.dto.add.AddAnswerDTO;
import ee.project.offline.quiz.domain.dto.quiz.AnswerDTO;
import ee.project.offline.quiz.domain.dto.results.QuizAnswer;

import java.util.ArrayList;
import java.util.List;

public class AnswerMapper {

    public static AnswerDTO fromDbToDto(Answer answerFromDb) {
        AnswerDTO answerDTO = new AnswerDTO();
        answerDTO.setId(answerFromDb.getId());
        answerDTO.setDescription(answerFromDb.getDescription());
        answerDTO.setAnswered(false);
        return answerDTO;
    }

    public static AddAnswerDTO fromDbToAddDto(Answer answerFromDb) {
        AddAnswerDTO answerDTO = new AddAnswerDTO();
        answerDTO.setId(answerFromDb.getId());
        answerDTO.setDescription(answerFromDb.getDescription());
        answerDTO.setPoints(answerFromDb.getPoints());
        answerDTO.setCorrectAnswer(answerFromDb.isCorrectAnswer());
        return answerDTO;
    }

    public static Answer fromDtoToDb(AddAnswerDTO answerDTO, Question question) {
        Answer answerToDb = new Answer();
        answerToDb.setId(answerDTO.getId());
        answerToDb.setDescription(answerDTO.getDescription());
        answerToDb.setCorrectAnswer(answerDTO.getCorrectAnswer());
        answerToDb.setPoints(answerDTO.getPoints());
        answerToDb.setQuestion(question);
        return answerToDb;
    }

    public static List<QuizAnswer> fromDtoToQuizAnswers(List<AnswerDTO> answers) {
        List<QuizAnswer> quizAnswers = new ArrayList<>();
        for (AnswerDTO answer : answers) {
            QuizAnswer qa = new QuizAnswer();
            qa.setAnswer(answer.getId());
            qa.setAnswered(answer.getAnswered());
            quizAnswers.add(qa);
        }
        return quizAnswers;
    }

    public static List<QuizAnswer> fromDbAnswerToQuizAnswer(List<Answer> answers) {
        List<QuizAnswer> quizAnswers = new ArrayList<>(answers.size());
        for (Answer answer : answers) {
            QuizAnswer qa = new QuizAnswer();
            qa.setAnswer(answer.getId());
            qa.setAnswered(false);
            quizAnswers.add(qa);
        }
        return quizAnswers;
    }
}
