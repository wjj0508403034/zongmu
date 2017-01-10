'use strict';

zongmu.controller("reasonCreateController", ["$scope", "rejectReasonService", "dialog", function($scope, rejectReasonService, dialog) {
  var params = $scope.ngDialogData;

  $scope.onSaveButtonClick = function() {
    if (!$scope.value) {
      dialog.showError("请填写原因描述");
      return;
    }

    rejectReasonService.createReason($scope.value)
      .then(function(res) {
        $scope.closeThisDialog({
          key: 'ok'
        });
      });
  };
}]);