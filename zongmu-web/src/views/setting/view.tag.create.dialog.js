'use strict';

zongmu.controller("viewTagCreateController", ["$scope", "viewTagService", "dialog",
  function($scope, viewTagService, dialog) {
    var params = $scope.ngDialogData;
    init();

    function init() {}

    $scope.onSaveButtonClick = function() {
      if(!$scope.value) {
        dialog.showError("请填写属性值!");
        return;
      }

      var data = {
        algorithmId: +params.algorithmId,
        name: $scope.value
      };
      viewTagService.create(data)
        .then(function(res) {
          $scope.closeThisDialog({
            key: 'ok'
          });
        });
    };
  }
]);