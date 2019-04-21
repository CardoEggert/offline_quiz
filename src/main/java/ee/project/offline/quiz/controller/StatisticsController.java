package ee.project.offline.quiz.controller;

import ee.project.offline.quiz.domain.statistics.StatisticsList;
import ee.project.offline.quiz.service.StatisticsService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsController {

    private final StatisticsService statisticsServiceService;

    public StatisticsController(StatisticsService statisticsServiceService) {
        this.statisticsServiceService = statisticsServiceService;
    }

    @Cacheable
    @GetMapping("/statistics")
    @CrossOrigin
    public StatisticsList getNewQuizWithUserInfo(){
        return statisticsServiceService.getStatistics();
    }
}
