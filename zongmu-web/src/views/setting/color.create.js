'use strict';

zongmu.controller("colorCreateController", ["$scope", "colorTagService", "dialog", function($scope, colorTagService, dialog) {
  var params = $scope.ngDialogData;

  initView();

  function initView() {
    if(params && params.color) {
      $scope.name = params.color.name;
      $scope.color = params.color.color;
    }
  }

  $scope.onSaveButtonClick = function() {
    if(!$scope.name) {
      dialog.showError("请填写属性名称");
      return;
    }

    if(!$scope.color) {
      dialog.showError("请选择颜色");
      return;
    }

    var colorTag = {
      name: $scope.name,
      color: $scope.color,
      colorGroupId: params.group.id
    };

    if(params && params.color) {
      colorTagService.updateColorTag(params.color.id, colorTag)
        .then(function(res) {
          $scope.closeThisDialog({
            key: 'ok'
          });
        });
    } else {
      colorTagService.createColorTag(colorTag)
        .then(function(res) {
          $scope.closeThisDialog({
            key: 'ok'
          });
        });
    }

  };
}]);