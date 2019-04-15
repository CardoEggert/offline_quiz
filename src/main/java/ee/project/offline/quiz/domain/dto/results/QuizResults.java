package ee.project.offline.quiz.domain.dto.results;

import java.util.List;

public class QuizResults {

    private Long quiz;
    private List<QuestionResultWrapper> questions;
    private Long points;
    private Long maxPoints;

    public QuizResults() {
    }

    public Long getQuiz() {
        return quiz;
    }

    public void setQuiz(Long quiz) {
        this.quiz = quiz;
    }

    public List<QuestionResultWrapper> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionResultWrapper> questions) {
        this.questions = questions;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public Long getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(Long maxPoints) {
        this.maxPoints = maxPoints;
    }
}
