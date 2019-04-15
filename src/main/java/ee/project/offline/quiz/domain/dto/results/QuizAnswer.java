package ee.project.offline.quiz.domain.dto.results;

public class QuizAnswer {
    private Long answer;
    private Boolean answered;

    public QuizAnswer() {
    }

    public QuizAnswer(Long answer, boolean answeredCorrectly) {
        this.answer = answer;
        this.answered = answeredCorrectly;
    }

    public Long getAnswer() {
        return answer;
    }

    public void setAnswer(Long answer) {
        this.answer = answer;
    }

    public Boolean getAnswered() {
        return answered;
    }

    public void setAnswered(Boolean answered) {
        this.answered = answered;
    }
}
