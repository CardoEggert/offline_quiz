package ee.project.offline.quiz.domain.statistics;

public class StatisticItem {

    private String name;
    private double percentageResult;

    public StatisticItem() {
    }

    public StatisticItem(String name, double percentageResult) {
        this.name = name;
        this.percentageResult = percentageResult;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPercentageResult() {
        return percentageResult;
    }

    public void setPercentageResult(double percentageResult) {
        this.percentageResult = percentageResult;
    }
}
