package ee.project.offline.quiz.controller;

import ee.project.offline.quiz.domain.Answer;
import ee.project.offline.quiz.domain.Quiz;
import ee.project.offline.quiz.domain.dto.QuestionDTO;
import ee.project.offline.quiz.domain.dto.QuizDTO;
import ee.project.offline.quiz.domain.dto.add.AddQuestionDTO;
import ee.project.offline.quiz.domain.dto.results.QuizResults;
import ee.project.offline.quiz.service.NotEnoughQuestionsException;
import ee.project.offline.quiz.service.QuizService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/quiz/new/{user}/{email}")
    @CrossOrigin
    public QuizDTO getNewQuizWithUserInfo(@PathVariable String user, @PathVariable String email) throws Exception{
        Quiz generatedQuiz = this.quizService.getNewQuiz(user, email);
        if (generatedQuiz == null) {
            throw new NotEnoughQuestionsException();
        }
        return this.quizService.convertQuiz(generatedQuiz);
    }

    @GetMapping("/quiz/new")
    @CrossOrigin
    public QuizDTO getNewQuiz() throws Exception{
        Quiz generatedQuiz = this.quizService.getNewQuiz();
        if (generatedQuiz == null) {
            throw new NotEnoughQuestionsException();
        }
        return this.quizService.convertQuiz(generatedQuiz);
    }

    @PostMapping("/add/question")
    @CrossOrigin
    public ResponseEntity<HttpStatus> addNewQuestionsToDatabase(@RequestBody AddQuestionDTO questionFromUser) {
        quizService.saveQuestion(questionFromUser);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/results")
    @CrossOrigin
    public QuizResults findOutQuizResults(@RequestBody QuizResults quizResults) {
        return quizService.validateResults(quizResults);
    }
}
