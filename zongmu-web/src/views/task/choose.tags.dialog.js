'use strict';

zongmu.controller("chooseTagsController", ['$scope', 'dialog', 'assetService', 'taskService',
  function($scope, dialog, assetService, taskService) {
    var params = $scope.ngDialogData;
    initData();

    function initData() {
      var map = {};
      angular.forEach(params.selectedTags || [], function(it, index) {
        map[it.id] = it;
      });

      assetService.getAssetTags()
        .then(function(tags) {
          $scope.tags = tags;
          angular.forEach($scope.tags, function(it, index) {
            it.isSelected = map[it.id] !== undefined;
          });
        });
    }

    $scope.$watch("allSelected", function() {
      angular.forEach($scope.tags, function(it, index) {
        it.isSelected = $scope.allSelected;
      });
    });

    $scope.onOkClick = function() {
      var selectedTags = [];
      angular.forEach($scope.tags, function(it, index) {
        if(it.isSelected) {
          selectedTags.push(it.id);
        }
      });

      if(params.type === "task") {
        taskService.updateAssetTags(params.taskNo, selectedTags)
          .then(function() {
            $scope.closeThisDialog({
              key: 'ok'
            });
          });
      } else {
        assetService.updateAssetTags(params.assetNo, selectedTags)
          .then(function() {
            $scope.closeThisDialog({
              key: 'ok'
            });
          });
      }
    };

  }
]);