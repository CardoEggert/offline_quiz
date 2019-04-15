package ee.project.offline.quiz.domain.dto.add;

import ee.project.offline.quiz.domain.dto.AnswerDTO;

import java.util.List;

public class AddQuestionDTO {

    private Long id;
    private String description;
    private String picturePath;
    private Boolean multipleChoice;
    private List<AddAnswerDTO> answers;

    public AddQuestionDTO() {
    }

    public AddQuestionDTO(int id) {
        this.id = (long) id;
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
        return multipleChoice;
    }

    public void setMultipleChoice(Boolean multipleChoice) {
        this.multipleChoice = multipleChoice;
    }

    public List<AddAnswerDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AddAnswerDTO> answers) {
        this.answers = answers;
    }
}
