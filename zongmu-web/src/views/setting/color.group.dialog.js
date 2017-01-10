'use strict';

zongmu.controller("colorGroupDialogController", ["$scope", "colorGroupService", "dialog",
  function($scope, colorGroupService, dialog) {
    var params = $scope.ngDialogData;

    initView();

    function initView() {
      if(params.algorithm.colorGroup) {
        $scope.name = params.algorithm.colorGroup.name;
      }
    }

    $scope.onSaveButtonClick = function() {
      if(!$scope.name) {
        dialog.showError("名称不能为空！");
        return;
      }

      var group = {
        name: $scope.name,
        algorithmId: params.algorithm.id
      };

      if(params.algorithm.colorGroup) {
        colorGroupService.update(params.algorithm.id, group)
          .then(function() {
            $scope.closeThisDialog({
              key: 'ok'
            });
          });
      } else {
        colorGroupService.create(group)
          .then(function() {
            $scope.closeThisDialog({
              key: 'ok'
            });
          });
      }
    };
  }
]);