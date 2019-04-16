package ee.project.offline.quiz.service;

import ee.project.offline.quiz.domain.Answer;
import ee.project.offline.quiz.domain.Question;
import ee.project.offline.quiz.domain.Quiz;
import ee.project.offline.quiz.domain.dto.quiz.AnswerDTO;
import ee.project.offline.quiz.domain.dto.quiz.QuestionDTO;
import ee.project.offline.quiz.domain.dto.quiz.QuizDTO;
import ee.project.offline.quiz.domain.dto.add.AddAnswerDTO;
import ee.project.offline.quiz.domain.dto.add.AddQuestionDTO;
import ee.project.offline.quiz.domain.dto.results.QuestionResultWrapper;
import ee.project.offline.quiz.domain.dto.results.QuizAnswer;
import ee.project.offline.quiz.domain.dto.results.QuizResults;
import ee.project.offline.quiz.domain.log.UserAnswerLog;
import ee.project.offline.quiz.domain.log.UserQuestionLog;
import ee.project.offline.quiz.mapper.AnswerMapper;
import ee.project.offline.quiz.mapper.QuestionMapper;
import ee.project.offline.quiz.mapper.QuizMapper;
import ee.project.offline.quiz.repository.AnswerRepository;
import ee.project.offline.quiz.repository.QuestionRepository;
import ee.project.offline.quiz.repository.QuizRepository;
import ee.project.offline.quiz.repository.log.UserAnswerLogRepository;
import ee.project.offline.quiz.repository.log.UserQuestionLogRepository;
import ee.project.offline.quiz.service.exceptions.InvalidQuestionException;
import ee.project.offline.quiz.service.exceptions.NotEnoughQuestionsException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private QuizRepository quizRepository;

    private QuestionRepository questionRepository;

    private AnswerRepository answerRepository;

    private UserQuestionLogRepository userQuestionLogRepository;

    private UserAnswerLogRepository userAnswerLogRepository;

    QuizService(QuizRepository quizRepository, QuestionRepository questionRepository, AnswerRepository answerRepository,
                       UserQuestionLogRepository userQuestionLogRepository, UserAnswerLogRepository userAnswerLogRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.userQuestionLogRepository = userQuestionLogRepository;
        this.userAnswerLogRepository = userAnswerLogRepository;
    }


    public Quiz getNewQuizWithUserInfo(String name, String email) {
        Quiz generateQuiz = getNewQuiz();
        generateQuiz.setUser(name, email);
        return generateQuiz;
    }

    public Quiz getNewQuiz() {
        List<Question> questionsForQuiz = questionRepository.findAll();
        if (questionsForQuiz == null || questionsForQuiz.size() < 10) {
            throw new NotEnoughQuestionsException();
        }
        Quiz quizWithQuestions = new Quiz(new HashSet<>(randomlyPickQuestions(questionsForQuiz)));
        quizWithQuestions.setMaxPoints(calculateMaxPoints(quizWithQuestions));
        return quizRepository.save(quizWithQuestions);
    }

    private Long calculateMaxPoints(Quiz quizWithQuestions) {
        if (!CollectionUtils.isEmpty(quizWithQuestions.getQuestions())) {
            long maxPoints = 0L;
            for (Question question : quizWithQuestions.getQuestions()) {
                List<Answer> answersFromDb = answerRepository.findAllByQuestionId(question.getId());
                for (Answer answer : answersFromDb) {
                    maxPoints += Math.max(0L, answer.getPoints());
                }
            }
            return maxPoints;
        }
        return null;
    }

    private List<Question> randomlyPickQuestions(List<Question> allQuestionsForQuiz) {
        Set<Question> pickedOutQuestions = new HashSet<>();
        while (pickedOutQuestions.size() != 10) {
            pickedOutQuestions.add(pickRandomQuestion(allQuestionsForQuiz));
    }
        return new ArrayList<>(pickedOutQuestions);
    }

    private Question pickRandomQuestion(List<Question> questionsForQuiz) {
        int random = (int)(Math.random() * questionsForQuiz.size());
        return questionsForQuiz.get(random);
    }

    public void saveQuestion(AddQuestionDTO userQuestion) {
        if (!isValidQuestion(userQuestion)) throw new InvalidQuestionException();
        List<AddAnswerDTO> userAnswers = userQuestion.getAnswers();
        Question parsedQuestion = QuestionMapper.fromAddDtoToDb(userQuestion);
        Question savedQuestion = questionRepository.saveAndFlush(parsedQuestion);
        answerRepository.saveAll(userAnswers.stream().map(answerDTO ->
                AnswerMapper.fromDtoToDb(answerDTO, savedQuestion)).collect(Collectors.toList()));
    }

    private boolean isValidQuestion(AddQuestionDTO userQuestion) {
        if (userQuestion.getMultipleChoice()) {
            return checkIfMoreThanAtleastZeroCorrect(userQuestion.getAnswers());
        }
        return checkIfOneCorrect(userQuestion.getAnswers());

    }

    private boolean checkIfMoreThanAtleastZeroCorrect(List<AddAnswerDTO> answers) {
        return answers.stream().anyMatch(AddAnswerDTO::getCorrectAnswer);
    }

    private boolean checkIfOneCorrect(List<AddAnswerDTO> answers) {
        return answers.stream().filter(AddAnswerDTO::getCorrectAnswer).count() == 1;
    }

    private List<QuestionDTO> getAnswersForQuestions(List<Question> questions) {
        if (!CollectionUtils.isEmpty(questions)) {
            List<QuestionDTO> questionDTOs = new ArrayList<>(10);
            for (Question question : questions) {
                List<Answer> answersToQuestions = this.answerRepository.findAllByQuestionId(question.getId());
                List<AnswerDTO> mappedAnswers = answersToQuestions.stream().map(AnswerMapper::fromDbToDto).collect(Collectors.toList());
                QuestionDTO questionDTO = QuestionMapper.fromDbToDto(question, mappedAnswers);
                questionDTOs.add(questionDTO);
            }
            return questionDTOs;
        }
        return null;
    }

    public QuizDTO convertQuiz(Quiz generatedQuiz) {
        return QuizMapper.fromDbToDto(generatedQuiz, getAnswersForQuestions(new ArrayList<>(generatedQuiz.getQuestions())));
    }

    public QuizResults validateResults(QuizResults generatedQuizResults) {
        Map<Long, Answer> answerMap = answerRepository.findAll().stream().collect(Collectors.toMap(Answer::getId, y -> y));
        QuizResults quizResults = checkResults(generatedQuizResults, answerMap);
        updateQuiz(generatedQuizResults.getQuiz(), quizResults.getPoints());
        updateLogs(quizResults, answerMap);
        return quizResults;
    }

    private void updateLogs(QuizResults quizResults, Map<Long, Answer> answerMap) {
        for(QuestionResultWrapper question : quizResults.getQuestions()) {
            UserQuestionLog uQuestionLog = new UserQuestionLog();
            uQuestionLog.setQuestion(question.getQuestion());
            uQuestionLog.setMaxPoints(quizResults.getMaxPoints());
            uQuestionLog.setPoints(quizResults.getPoints());
            uQuestionLog.setQuiz(quizResults.getQuiz());
            uQuestionLog.setUserAnsweredCorrect(question.getAnswers().stream().allMatch(QuizAnswer::getAnswered));
            UserQuestionLog savedLog = userQuestionLogRepository.saveAndFlush(uQuestionLog);
            for (QuizAnswer answer : question.getAnswers()) {
                UserAnswerLog uAnswerLog = new UserAnswerLog();
                Answer dbAnswer = getAnswerFromDb(answerMap, answer.getAnswer());
                uAnswerLog.setPoints(dbAnswer.getPoints());
                uAnswerLog.setUserAnsweredCorrect(answer.getAnswered());
                uAnswerLog.setUserQuestionLog(savedLog);
                userAnswerLogRepository.save(uAnswerLog);
            }
        }
    }

    private QuizResults checkResults(QuizResults userInput, Map<Long, Answer> answerMap) {
        QuizResults checkedResult = new QuizResults();
        checkedResult.setQuiz(userInput.getQuiz());
        checkedResult.setMaxPoints(quizRepository.getOne(userInput.getQuiz()).getMaxPoints());
        checkedResult.setPoints(calculateResults(userInput.getQuestions(), answerMap, checkedResult));
        return checkedResult;
    }

    private long calculateResults(List<QuestionResultWrapper> questions, Map<Long, Answer> answerMap, QuizResults checkedResult) {
        long result = 0L;
        List<QuestionResultWrapper> checkedQuestions = new ArrayList<>(questions.size());
        for (QuestionResultWrapper qWrapper : questions) {
            List<QuizAnswer> checkedAnswers = calculateResult(qWrapper.getAnswers(), answerMap, qWrapper.getMultipleChoice());
            checkedQuestions.add(new QuestionResultWrapper(qWrapper.getQuestion(), qWrapper.getMultipleChoice(),
                    checkedAnswers, qWrapper.getMultipleChoice() ? null : getCorrectAnswer(qWrapper.getAnswers(), answerMap)));
            result += addPointsAccordingToCheckedAnswers(checkedAnswers, answerMap);
        }
        checkedResult.setQuestions(checkedQuestions);
        return result;
    }

    private QuizAnswer getCorrectAnswer(List<QuizAnswer> answers, Map<Long, Answer> answerMap) {
        for (QuizAnswer answer : answers) {
            Answer dbAnswer = getAnswerFromDb(answerMap, answer.getAnswer());
            if (dbAnswer.isCorrectAnswer()) return new QuizAnswer(dbAnswer.getId(), dbAnswer.isCorrectAnswer());
        }
        return null;
    }

    private long addPointsAccordingToCheckedAnswers(List<QuizAnswer> checkedAnswers, Map<Long, Answer> answerMap) {
        long result = 0L;
        for(QuizAnswer checkedAnswer : checkedAnswers) {
            if (checkedAnswer.getAnswered()) {
                Answer dbAnswer = getAnswerFromDb(answerMap, checkedAnswer.getAnswer());
                if (dbAnswer != null) result += dbAnswer.getPoints();
            }
        }
        return result;
    }

    private List<QuizAnswer> calculateResult(List<QuizAnswer> answers, Map<Long, Answer> answerMap, Boolean isMultipleChoice) {
        List<QuizAnswer> answeredLog = new ArrayList<>(answers.size());
        for (QuizAnswer answer : answers) {
            Answer dbAnswer = getAnswerFromDb(answerMap, answer.getAnswer());
            if (dbAnswer != null) {
                boolean answeredCorrectly = checkAnswer(dbAnswer, answer);
                answeredLog.add(new QuizAnswer(answer.getAnswer(), answeredCorrectly));
            }
        }
        if (!isMultipleChoice && answeredLog.stream().anyMatch(x -> !x.getAnswered())) {
            return allFalseArray(answers);
        }
        return answeredLog;
    }

    private boolean checkAnswer(Answer dbAnswer, QuizAnswer answer) {
        return dbAnswer.isCorrectAnswer() == answer.getAnswered();
    }

    private Answer getAnswerFromDb(Map<Long, Answer> answerMap, Long answer) {
        Answer dbAnswer = answerMap.get(answer);
        if (dbAnswer == null) {
            dbAnswer = fallbackMethod(answer);
        }
        return dbAnswer;
    }

    private Answer fallbackMethod(Long answerId) {
        return answerRepository.getOne(answerId);
    }

    private List<QuizAnswer> allFalseArray(List<QuizAnswer> answers) {
        List<QuizAnswer> copyOfAnswers = new ArrayList<>(answers);
        for (QuizAnswer copyOfAnswer : copyOfAnswers) {
            if (copyOfAnswer.getAnswered()) {
                copyOfAnswer.setAnswered(false);
            }
        }
        return copyOfAnswers;
    }

    private void updateQuiz(Long quiz, long result) {
        Quiz dbQuiz = quizRepository.getOne(quiz);
        dbQuiz.setCompleted(true);
        dbQuiz.setResult(result);
    }
}
