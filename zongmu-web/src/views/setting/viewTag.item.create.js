'use strict';

zongmu.controller("NewViewTagItemCreateController", ["$scope", "viewTagService", "dialog",
  function($scope, viewTagService, dialog) {
    var params = $scope.ngDialogData;

    $scope.onSaveButtonClick = function() {
      if(!$scope.value) {
        dialog.showError("请填写属性值!");
        return;
      }

      var data = {
        names: $scope.value.split(";").filter(function(it) {
          return it;
        })
      };
      viewTagService.batchCreateItems(params.viewTagId, data)
        .then(function(res) {
          $scope.closeThisDialog({
            key: 'ok'
          });
        });
    };
  }
]);