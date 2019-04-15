package ee.project.offline.quiz.controller.wrapper;

public class FilePathWrapper {
    private String filePath;

    public FilePathWrapper(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
