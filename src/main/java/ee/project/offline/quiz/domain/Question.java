package ee.project.offline.quiz.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
public class Question {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String description;

    @Column(name = "picture_path")
    private String picturePath;

    @Column(name = "is_multiple_choice")
    private Boolean isMultipleChoice;

    public Question() {
    }

    public Question(String description, boolean isMultipleChoice) {
        this.description = description;
        this.isMultipleChoice = isMultipleChoice;
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

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public Boolean getMultipleChoice() {
        return isMultipleChoice;
    }

    public void setMultipleChoice(Boolean multipleChoice) {
        isMultipleChoice = multipleChoice;
    }
}
