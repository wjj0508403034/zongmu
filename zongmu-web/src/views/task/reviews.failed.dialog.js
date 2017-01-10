'use strict';

zongmu.controller("reviewFailedDialogController", ['$scope', 'dialog', 'taskRecordService', 'rejectReasonService', 'reviewRecordService',
  function($scope, dialog, taskRecordService, rejectReasonService, reviewRecordService) {
    var params = $scope.ngDialogData;

    init();

    $scope.onOkClick = function() {
      if(!$scope.data.reason) {
        dialog.showError("请选择原因！");
        return;
      }
      var data = {
        reasonId: $scope.data.reason.id,
        memo: $scope.data.memo
      };

      if(params.batch) {
        data.reviewRecordNos = params.reviewRecordNos;
        reviewRecordService.batchReviewFailed(data)
          .then(function(res) {
            $scope.closeThisDialog({
              key: 'ok'
            });
          });
      } else {
        taskRecordService.reviewFail(params.reviewRecord.taskRecordNo, data)
          .then(function() {
            $scope.closeThisDialog({
              key: 'ok'
            });
          });
      }

    };

    function init() {
      $scope.data = {};
      rejectReasonService.getReasons()
        .then(function(data) {
          $scope.reasons = data;
          $scope.reasons.forEach(function(it) {
            if(it.default) {
              $scope.data.reason = it;
            }
          });
          if(!$scope.data.reason && $scope.reasons.length > 0) {
            $scope.data.reason = $scope.reasons[0];
          }
        });
    }

  }
]);