'use strict';

zongmu.controller("assetViewTagItemCreateController", ["$scope", "assetViewTagService", "dialog",
  function($scope, assetViewTagService, dialog) {
    var params = $scope.ngDialogData;

    $scope.onSaveButtonClick = function() {
      if(!$scope.value) {
        dialog.showError("请填写属性值!");
        return;
      }

//    if(!passwordUtils.batchCheckTag($scope.value)) {
//      dialog.showError("属性只能有中划线下划线英文字母中文字母或者数字组成!");
//      return;
//    }

      var data = {
        assetViewTagId: params.assetViewTagId,
        items: $scope.value.split(";").filter(function(it) {
          return it;
        })
      };
      assetViewTagService.batchCreateItems(data)
        .then(function(res) {
          $scope.closeThisDialog({
            key: 'ok'
          });
        });
    };
  }
]);