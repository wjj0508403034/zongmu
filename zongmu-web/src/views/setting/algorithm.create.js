'use strict';

zongmu.controller("algorithmCreateController", ["$scope", "algorithmService", "dialog", "passwordUtils",
  function($scope, algorithmService, dialog, passwordUtils) {
    var params = $scope.ngDialogData;
    $scope.isNew = true;

    if(params && params.algorithm) {
      $scope.name = params.algorithm.name;
      $scope.isNew = false;
    }

    $scope.onSaveButtonClick = function() {
      if(!$scope.name) {
        dialog.showError("请填写算法名称");
        return;
      }

      if(!passwordUtils.checkTag($scope.name)) {
        dialog.showError("名称只能有中划线下划线英文字母中文字母或者数字组成!");
        return;
      }

      if(!$scope.isNew) {
        algorithmService.updateAlgorithm(params.algorithm.id, {
          name: $scope.name
        }).then(function(res) {
          $scope.closeThisDialog({
            key: 'ok'
          });
        });
      } else {
        algorithmService.createAlgorithm({
          name: $scope.name
        }).then(function(res) {
          $scope.closeThisDialog({
            key: 'ok'
          });
        });
      }

    };
  }
]);