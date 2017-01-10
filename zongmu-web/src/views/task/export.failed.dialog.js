'use strict';

zongmu.controller("exportFailedDialogController", ['$scope','dialog',
  function($scope, dialog) {    

    $scope.taskItems = $scope.ngDialogData.taskItems;

  }
]);