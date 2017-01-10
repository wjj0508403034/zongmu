'use strict';

zongmu.controller("chooseTaskPriorityController", ['$scope', 'dialog', 'taskService', 'enumService',
  function($scope, dialog, taskService, enumService) {
    var params = $scope.ngDialogData;
    $scope.task = params.task;
    $scope.taskPriority = $scope.task.priority;
    $scope.taskPriorities = enumService.getTaskPriorities();
    $scope.onOkClick = function() {
      taskService.setPriority($scope.task.taskNo, $scope.taskPriority)
        .then(function() {
          $scope.closeThisDialog({
            key: 'ok'
          });
        });
    };

  }
]);