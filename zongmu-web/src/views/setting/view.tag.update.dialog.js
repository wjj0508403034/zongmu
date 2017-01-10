'use strict';

zongmu.controller("viewTagUpdateController", ["$scope", "viewTagService", "dialog",
  function($scope, viewTagService, dialog) {
    var params = $scope.ngDialogData;
    init();

    function init() {
      $scope.value = params.viewTag.name;
    }

    $scope.onSaveButtonClick = function() {
      if(!$scope.value) {
        dialog.showError("请填写属性值!");
        return;
      }

      var data = {
        name: $scope.value
      };
      viewTagService.update(params.viewTag.id, data)
        .then(function(res) {
          $scope.closeThisDialog({
            key: 'ok'
          });
        });
    };
  }
]);