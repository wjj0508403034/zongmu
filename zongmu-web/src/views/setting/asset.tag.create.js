'use strict';

zongmu.controller("assetTagCreateController", ["$scope", "assetService", "enumService", "dialog",
  function($scope, assetService, enumService, dialog) {

    init();

    function init() {
      $scope.category = "ROAD";
      $scope.categories = enumService.getTagCategories();
    }

    $scope.onSaveButtonClick = function() {
      if(!$scope.value) {
        dialog.showError("请填写属性值!");
        return;
      }

      var data = {
        category: $scope.category,
        value: $scope.value
      };
      assetService.batchCreateAssetTags(data)
        .then(function(res) {
          $scope.closeThisDialog({
            key: 'ok'
          });
        });
    };
  }
]);