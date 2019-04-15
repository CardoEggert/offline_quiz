package ee.project.offline.quiz.domain.log;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class UserQuestionLog {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "question_id")
    private Long question;

    @Column(name = "quiz_id")
    private Long quiz;

    @Column(name = "user_answered_correct")
    private Boolean userAnsweredCorrect;

    private Long points;

    @Column(name = "max_points")
    private Long maxPoints;

    public UserQuestionLog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuestion() {
        return question;
    }

    public void setQuestion(Long question) {
        this.question = question;
    }

    public Long getQuiz() {
        return quiz;
    }

    public void setQuiz(Long quiz) {
        this.quiz = quiz;
    }

    public Boolean getUserAnsweredCorrect() {
        return userAnsweredCorrect;
    }

    public void setUserAnsweredCorrect(Boolean userAnsweredCorrect) {
        this.userAnsweredCorrect = userAnsweredCorrect;
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
