package ee.project.offline.quiz.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
public class Quiz {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "is_completed")
    private Boolean isCompleted;

    private Long result;

    @Column(name = "max_points")
    private Long maxPoints;

    @ManyToMany
    @JoinTable(
            name = "",
            joinColumns = @JoinColumn(name = "quiz_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id"))
    private Set<Question> questions;

    public Quiz() {
    }

    public Quiz(Set<Question> questions) {
        this.questions = questions;
        this.isCompleted = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Boolean getCompleted() {
        return isCompleted;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
    }

    public Long getResult() {
        return result;
    }

    public void setResult(Long result) {
        this.result = result;
    }

    public Long getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(Long maxPoints) {
        this.maxPoints = maxPoints;
    }

    public Set<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }

    public void setUser(String name, String email) {
        this.userName = name;
        this.userEmail = email;
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", isCompleted=" + isCompleted +
                ", result=" + result +
                ", maxPoints=" + maxPoints +
                ", questions=" + questions +
                '}';
    }
}
