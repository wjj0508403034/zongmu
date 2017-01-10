'use strict';

zongmu.controller("tagValueCreateController", ["$scope", "tagService", "dialog", function($scope, tagService, dialog) {
  var params = $scope.ngDialogData;

  $scope.onSaveButtonClick = function() {
    if(!$scope.value){
      dialog.showError("请填写属性值");
      return;
    }
    
    var tagItems = $scope.value.split(";");
    if (params && params.tag && params.tag.id) {
      tagService.batchAddTagItem(params.tag.id, tagItems)
        .then(function(res) {
          $scope.closeThisDialog({
            key: 'ok'
          });
        });
    } else {
      dialog.showError("参数错误");
    }
  };
}]);