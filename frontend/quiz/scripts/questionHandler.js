var app = angular.module('questionHandlerApp', []);

app.controller('QuestionHandlerController', ['$scope', '$http', function($scope, $http) {
    $scope.questionDescription = '';
    $scope.questonHasPicture = false;
    $scope.multipleChoice = false;
    $scope.questionFilePath = '';
    $scope.answerId = 1;
    
    $scope.answers = [
        { id: $scope.answerId, description: '', points: 0, correctAnswer: false }
    ];
    
    $scope.addAnswer = function() {
        $scope.answerId = $scope.answerId +1;
        $scope.answers.push({ id: $scope.answerId, description: '', points: 0, correctAnswer: false });
    };
    
    $scope.sendQuestion = function() {
        if (answersLegit($scope.multipleChoice, $scope.answers)) {
        var parameter = JSON.stringify({
            id: null, 
            description: $scope.questionDescription,
            picturePath: $scope.questionFilePath,
            multipleChoice: $scope.multipleChoice,
            answers: nullEachId($scope.answers)
            });
            $http.post("http://localhost:8080/add/question", parameter).then(function(response) {
                console.log(response);
                console.log("success");
            }, function(error) {
                console.log(error);
                console.log("failed");
            });
        }
    };
    
    emptyQuestionFiels = function() {
        $scope.questionDescription = '';
        $scope.questonHasPicture = false;
        $scope.multipleChoice = false;
        $scope.questionFilePath = '';
        $scope.answerId = 1;
        $scope.answers = [
        { id: $scope.answerId, description: '', points: 0, correctAnswer: false }
        ];
    }
        
    answersLegit = function(multipleChoices, answerArray) {
        if (answerArray && answerArray.length) {
            var countCorrectAnswers = 0;
            for(i=0;i<answerArray.length;i++) { 
                if (answerArray[i].correctAnswer) {
                    countCorrectAnswers = countCorrectAnswers + 1;
                }
            }   
            if (multipleChoices) {
                return countCorrectAnswers > 0;
            }
                return countCorrectAnswers == 1;
            }
        return false

    }
        
    $scope.uploadFile = function(files) {
        var fd = new FormData();
        //Take the first selected file
        fd.append("file", files[0]);

        $http.post("http://localhost:8080/upload", fd, {
                withCredentials: false,
                headers: {'Content-Type': undefined },
                transformRequest: angular.identity
            }).then(function (response){
                $scope.questionFilePath = response.data["filePath"];
            },function (error){
                console.log(error);
            });
        
    };
    
    nullEachId = function(answerArray) {
        for(i=0;i<answerArray.length;i++) { 
            answerArray[i].id = null;
        }
        return answerArray;
    };
    
    $scope.questionSubmittable = function() {
        return $scope.questionForm.$dirty && $scope.questionForm.$valid;
    };
        
}]);