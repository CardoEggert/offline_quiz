var app = angular.module('QuizApp', []);

app.controller('quizController', ['$scope', '$http', function($scope, $http) {
    $scope.startQuiz = false;
    $scope.quizInProgress = false;
    $scope.name = '';
    $scope.email = '';
    $scope.quizFinished = false;
    
    $scope.quiz = null;
    
    $scope.clearFormValues = function() {
        $scope.name = '';
        $scope.email = '';
    };
    
    $scope.startQuizApp = function(name, email) {
        $scope.startQuiz = true;
        $scope.quizInProgress = true;
        
        if ($scope.acceptUserInfo) {
            $http.get('http://localhost:8080/quiz/new/'+name+'/'+email).then(function (response){
                $scope.quiz = response.data;
                $scope.setSelected(0);
            },function (error){
                console.log(error);
            });
        } else {
            $http.get('http://localhost:8080/quiz/new').then(function (response){
                $scope.quiz = response.data;
                $scope.setSelected(0);
            },function (error){
                console.log(error);
            });
        }
    };
    
    $scope.questionsAnswered = function(quiz) {
        if (quiz && quiz.questions) {
            return $scope.quiz.questions.filter($scope.isQuestionAnswered).length;
        }
        return 0;
    };
    
    $scope.isQuestionAnswered = function(question) {
        return question.answers.filter(answer => answer.answered).length > 0;
    };
    
    $scope.isQuestionAnsweredCorrectly = function(question, quizResults) {
        if (quizResults) {    
            for (var i = 0; i < quizResults.questions.length; i++) {
                if (quizResults.questions[i].question == question.id) {
                    for (var j = 0; j < quizResults.questions[i].answers.length; j++ ) {
                        if (!quizResults.questions[i].answers[j].answered) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
    };
    
    
    $scope.isAnswerAnsweredCorrectly = function(question, quizResults, answer) {
        if (quizResults) {    
            for (var i = 0; i < quizResults.questions.length; i++) {
                if (quizResults.questions[i].question == question.id) {
                    for (var j = 0; j < quizResults.questions[i].answers.length; j++ ) {
                        if (answer.id == quizResults.questions[i].answers[j].answer) {
                            return quizResults.questions[i].answers[j].answered;
                        }
                    }
                }
            }
            return true;
        }
    };
    
    
    $scope.isRadioAnswerAnsweredCorrectly = function(question, quizResults, answer) {
        if (quizResults) {    
            for (var i = 0; i < quizResults.questions.length; i++) {
                if (quizResults.questions[i].question == question.id && quizResults.questions[i].singleChoiceAnswer) {
                    return quizResults.questions[i].singleChoiceAnswer.answer == answer.id
                }
            }
            return true;
        }
    };
    
    
    $scope.setSelected = function(index) {
        if (index < 0 ) {
            index = 9;
        } else if (index > 9) {
            index = 0;
        }
        $scope.selected = index;
        if ($scope.quiz && $scope.quiz.questions) {
            $scope.selectedQuestion = $scope.quiz.questions[index];
            $scope.getQuestionPicture($scope.selectedQuestion);
        }
    };
    
    
    $scope.testStartable = function() {
        if ($scope.acceptUserInfo) {
            if ($scope.beginForm.$dirty && $scope.beginForm.$valid) {
                return true;
            } else {
                return false;
            }
            
        }
        return true;
    };
    
    $scope.checkOnlyOne = function(answer, answers) {
        for (var indx = 0; indx < answers.length; indx++) {
            answers[indx].answered = answers[indx].id === answer.id;
        }
        
    };
    
    $scope.getQuestionPicture = function(question) {
        if (question.picturePath != '') {
            $scope.selectedQuestionPicture = 'http://localhost:8080/files/' + question.picturePath;
        }
    };
    
    $scope.submitTest = function() {
        var parameter = JSON.stringify({
        quiz: $scope.quiz.id, 
        maxPoints: $scope.quiz.maxPoints,
        questions: parseQuestions($scope.quiz.questions)
        });
        $http.post("http://localhost:8080/results", parameter).then(function(response) {
                $scope.quizResults = response.data;
                $scope.quizInProgress = false;
                $scope.quizFinished = true;
                console.log("success");
            }, function(error) {
                console.log(error);
                console.log("failed");
            });
    };
    
    parseQuestions = function(questions) {
        return questions.map(question => { 
            var returnedQuestion = {};
            returnedQuestion["question"] = question.id;
            returnedQuestion["multipleChoice"] = question.multipleChoice;
            returnedQuestion["answers"] = parseAnswers(question.answers);
            returnedQuestion["singleChoiceAnswer"] = null;
            return returnedQuestion;
        });
    };
    
    parseAnswers = function(answers) {
        return answers.map(answer => {
            var returnedAnswer = {};
            returnedAnswer["answer"] = answer.id;
            returnedAnswer["answered"] = answer.answered;
            return returnedAnswer;
        });
    };
    
    $scope.questionSolvedCorrectly = function(question) {
        if ($scope.quizFinished && $scope.quizResults && question) {    
            var selectedQuestionResult = $scope.quizResults.questions.filter(x => x.id == question.id)[0];
            if (!selectedQuestionResult.multipleChoice) {
                return selectedQuestionResult.answers[0];
            } else {
                return selectedQuestionResult.answers.filter(X => x.answered ).length == selectedQuestionResult.answers.length;
            }
        }
    };
    
    $scope.closeTest = function () {
        $scope.acceptUserInfo = true;
        $scope.startQuiz = false;
        $scope.quizInProgress = false;
        $scope.quizFinished = false;
        $scope.quizResults = undefined;
        $scope.quiz = undefined;
    };
    
<<<<<<< Updated upstream
=======
    $scope.hgt = $window.innerHeight / 2;
    $scope.screenWidthQuarter = $window.innerWidth / 4;
    
    
    $scope.loadStatistics = function() {
        $http.get("http://localhost:8080/statistics").then(function(response) {
                $scope.statistics = response.data;
                console.log("success");
            }, function(error) {
                console.log(error);
                console.log("failed");
            });
    }
    
    $scope.statisticsToShow = function () {
        if ($scope.statistics && $scope.statistics.statisticItems.length > 0) {
            return true;
        }
        return false;
    }
    
    $interval(function() {
        if (!$scope.startQuiz && !$scope.quizInProgress) {
            $scope.loadStatistics();
        }
    }, 60000);
    
>>>>>>> Stashed changes
    
}]);