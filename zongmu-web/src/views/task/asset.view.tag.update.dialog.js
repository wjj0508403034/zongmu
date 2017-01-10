'use strict';

zongmu.controller("assetViewTagUpdateDialogController", ['$scope', 'dialog', 'assetService', 'assetViewTagService',
  function($scope, dialog, assetService, assetViewTagService) {
    var params = $scope.ngDialogData;

    init();

    function init() {

      assetViewTagService.getAll()
        .then(function(tags) {
          $scope.viewTags = tags;
          $scope.viewTagsMap = {};
          params.asset.viewTags.forEach(function(it) {
            $scope.viewTagsMap[it.assetViewTagId] = it.assetViewTagItemId;
          });
        });
    }

    function getSelected() {
      var viewTags = [];

      Object.keys($scope.viewTagsMap).forEach(function(key) {
        if($scope.viewTagsMap[key] != null || $scope.viewTagsMap[key] != undefined) {
          viewTags.push({
            assetViewTagId: key,
            assetViewTagItemId: $scope.viewTagsMap[key]
          });
        }
      });

      return viewTags;
    }

    $scope.onSaveButtonClick = function() {
      assetService.updateAssetViewTags(params.asset.assetNo, {
          "items": getSelected()
        })
        .then(function() {
          $scope.closeThisDialog({
            key: 'ok'
          });
        });
    };
  }
]);