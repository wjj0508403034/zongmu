'use strict';

zongmu.controller("assetViewTagCreateController", ["$scope", "assetViewTagService", "dialog","passwordUtils",
  function($scope, assetViewTagService, dialog,passwordUtils) {

    var params = $scope.ngDialogData || {};
    if(params.tag) {
      $scope.value = params.tag.name;
    }

    $scope.onSaveButtonClick = function() {
      if(!$scope.value) {
        dialog.showError("请填写属性值!");
        return;
      }
      
      if(!passwordUtils.checkTag($scope.value)){
        dialog.showError("属性只能有中划线下划线英文字母中文字母或者数字组成!");
        return;
      }

      var data = {
        name: $scope.value
      };

      if(params.tag) {
        assetViewTagService.update(params.tag.id, data)
          .then(function(res) {
            $scope.closeThisDialog({
              key: 'ok'
            });
          });
      } else {
        assetViewTagService.create(data)
          .then(function(res) {
            $scope.closeThisDialog({
              key: 'ok'
            });
          });
      }

    };
  }
]);