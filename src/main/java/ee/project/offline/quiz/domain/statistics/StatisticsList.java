package ee.project.offline.quiz.domain.statistics;

import java.util.List;

public class StatisticsList {

    private List<StatisticItem> statisticItems;

    public StatisticsList() {
    }

    public StatisticsList(List<StatisticItem> statisticItems) {
        this.statisticItems = statisticItems;
    }

    public List<StatisticItem> getStatisticItems() {
        return statisticItems;
    }

    public void setStatisticItems(List<StatisticItem> statisticItems) {
        this.statisticItems = statisticItems;
    }
}
