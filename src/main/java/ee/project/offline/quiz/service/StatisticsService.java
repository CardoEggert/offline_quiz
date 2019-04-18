package ee.project.offline.quiz.service;

import ee.project.offline.quiz.domain.Quiz;
import ee.project.offline.quiz.domain.statistics.StatisticItem;
import ee.project.offline.quiz.domain.statistics.StatisticsList;
import ee.project.offline.quiz.repository.QuizRepository;
import ee.project.offline.quiz.service.exceptions.NotEnoughQuestionsException;
import net.bytebuddy.utility.RandomString;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    private QuizRepository quizRepository;

    StatisticsService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    public StatisticsList getStatistics() {
        if (!isEnoughtQuizSubmissions()) {
            throw new NotEnoughQuestionsException();
        }

        List<Quiz> dbQuizzes = quizRepository.findTopFiveBestResultingSubmissions();
        List<StatisticItem> statisticItems = dbQuizzes.stream().map(x ->
                new StatisticItem(generateName(), calculatePercentage(x))).collect(Collectors.toList());


        return new StatisticsList(statisticItems);
    }

    private String generateName() {
        return RandomString.make(10);
    }

    private double calculatePercentage(Quiz quizSubmission) {
        return (double) quizSubmission.getResult() / quizSubmission.getMaxPoints();
    }

    public boolean isEnoughtQuizSubmissions() {
        return quizRepository.isEnoughQuizSubmissionsForStatistics();
    }
}
