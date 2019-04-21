package ee.project.offline.quiz.mapper;

import ee.project.offline.quiz.domain.Answer;
import ee.project.offline.quiz.domain.Question;
import ee.project.offline.quiz.domain.dto.quiz.AnswerDTO;
import ee.project.offline.quiz.domain.dto.quiz.QuestionDTO;
import ee.project.offline.quiz.domain.dto.add.AddAnswerDTO;
import ee.project.offline.quiz.domain.dto.add.AddQuestionDTO;
import ee.project.offline.quiz.domain.dto.results.QuestionResultWrapper;

import java.util.*;

public class QuestionMapper {

    public static QuestionDTO fromDbToDto(Question questionFromDb, List<AnswerDTO> answersDTO) {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setId(questionFromDb.getId());
        questionDTO.setAnswers(answersDTO);
        questionDTO.setDescription(questionFromDb.getDescription());
        questionDTO.setPicturePath(questionFromDb.getPicturePath());
        questionDTO.setMultipleChoice(questionFromDb.getMultipleChoice());
        return questionDTO;
    }

    public static Question fromDtoToDb(QuestionDTO questionDTO) {
        Question questionToDb = new Question();
        questionToDb.setId(questionDTO.getId());
        questionToDb.setDescription(questionDTO.getDescription());
        questionToDb.setPicturePath(questionDTO.getPicturePath());
        questionToDb.setMultipleChoice(questionDTO.getMultipleChoice());
        return questionToDb;
    }

    public static AddQuestionDTO fromDbToAddDto(Question questionFromDb, List<AddAnswerDTO> answersDTO) {
        AddQuestionDTO questionDTO = new AddQuestionDTO();
        questionDTO.setId(questionFromDb.getId());
        questionDTO.setAnswers(answersDTO);
        questionDTO.setDescription(questionFromDb.getDescription());
        questionDTO.setPicturePath(questionFromDb.getPicturePath());
        questionDTO.setMultipleChoice(questionFromDb.getMultipleChoice());
        return questionDTO;
    }

    public static Question fromAddDtoToDb(AddQuestionDTO questionDTO) {
        Question questionToDb = new Question();
        questionToDb.setId(questionDTO.getId());
        questionToDb.setDescription(questionDTO.getDescription());
        questionToDb.setPicturePath(questionDTO.getPicturePath());
        questionToDb.setMultipleChoice(questionDTO.getMultipleChoice());
        return questionToDb;
    }

    public static List<QuestionResultWrapper> fromDtoToQuestionWrappe(List<QuestionDTO> questions) {
        List<QuestionResultWrapper> qrWrappers = new ArrayList<>();
        for (QuestionDTO question : questions) {
            QuestionResultWrapper qrWrapper = new QuestionResultWrapper();
            qrWrapper.setQuestion(question.getId());
            qrWrapper.setMultipleChoice(question.getMultipleChoice());
            qrWrapper.setAnswers(AnswerMapper.fromDtoToQuizAnswers(question.getAnswers()));
            qrWrapper.setSingleChoiceAnswer(null);
            qrWrappers.add(qrWrapper);
        }
        return qrWrappers;
    }

    public static List<QuestionResultWrapper> fromQuizQuestionsToQuestionResultWrapper(Set<Question> questions, Map<Long, List<Answer>> mapQuestionToAnswer) {
        List<QuestionResultWrapper> questionResultWrappers = new ArrayList<>(questions.size());
        for (Question question : questions) {
            QuestionResultWrapper qrw = new QuestionResultWrapper();
            qrw.setMultipleChoice(question.getMultipleChoice());
            qrw.setQuestion(question.getId());
            qrw.setAnswers(AnswerMapper.fromDbAnswerToQuizAnswer(mapQuestionToAnswer.get(question.getId())));
            questionResultWrappers.add(qrw);
        }
        return questionResultWrappers;
    }
}
