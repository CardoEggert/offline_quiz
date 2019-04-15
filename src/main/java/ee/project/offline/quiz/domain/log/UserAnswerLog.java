package ee.project.offline.quiz.domain.log;

import javax.persistence.*;

@Entity
public class UserAnswerLog {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "answer_id")
    private Long answer;

    @Column(name = "user_answered_correct")
    private Boolean userAnsweredCorrect;

    private Long points;

    @ManyToOne
    @JoinColumn(name = "user_question_log_id")
    private UserQuestionLog userQuestionLog;

    public UserAnswerLog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAnswer() {
        return answer;
    }

    public void setAnswer(Long answer) {
        this.answer = answer;
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

    public UserQuestionLog getUserQuestionLog() {
        return userQuestionLog;
    }

    public void setUserQuestionLog(UserQuestionLog userQuestionLog) {
        this.userQuestionLog = userQuestionLog;
    }
}
