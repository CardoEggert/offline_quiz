package ee.project.offline.quiz.domain.dto.results;

import java.util.List;

public class QuestionResultWrapper {
    private Long question;
    private Boolean multipleChoice;
    private List<QuizAnswer> answers;
    private QuizAnswer singleChoiceAnswer;

    public QuestionResultWrapper() {
    }

    public QuestionResultWrapper(Long question, Boolean multipleChoice, List<QuizAnswer> answers, QuizAnswer singleChoiceAnswer) {
        this.question = question;
        this.multipleChoice = multipleChoice;
        this.answers = answers;
        this.singleChoiceAnswer = singleChoiceAnswer;
    }

    public Long getQuestion() {
        return question;
    }

    public void setQuestion(Long question) {
        this.question = question;
    }

    public Boolean getMultipleChoice() {
        return multipleChoice;
    }

    public void setMultipleChoice(Boolean multipleChoice) {
        this.multipleChoice = multipleChoice;
    }

    public List<QuizAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<QuizAnswer> answers) {
        this.answers = answers;
    }

    public QuizAnswer getSingleChoiceAnswer() {
        return singleChoiceAnswer;
    }

    public void setSingleChoiceAnswer(QuizAnswer singleChoiceAnswer) {
        this.singleChoiceAnswer = singleChoiceAnswer;
    }
}
