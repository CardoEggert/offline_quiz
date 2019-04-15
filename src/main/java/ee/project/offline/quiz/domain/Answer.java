package ee.project.offline.quiz.domain;

import org.springframework.context.annotation.Bean;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Answer {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String description;

    @NotNull
    private Long points;

    @Column(name = "is_correct_answer")
    private boolean isCorrectAnswer;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    public Answer() {
    }

    public Answer(String description) {
        this.description = description;
    }

    public Answer(String description, boolean isCorrectAnswer, int points) {
        this.description = description;
        this.isCorrectAnswer = isCorrectAnswer;
        this.points = (long) points;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public boolean isCorrectAnswer() {
        return isCorrectAnswer;
    }

    public void setCorrectAnswer(boolean correctAnswer) {
        isCorrectAnswer = correctAnswer;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
