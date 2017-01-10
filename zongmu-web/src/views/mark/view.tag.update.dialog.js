'use strict';

zongmu.controller("viewTagUpdateDialogController", ['$scope', 'dialog', 'assetService', 'taskService',
  function($scope, dialog, assetService, taskService) {
    var params = $scope.ngDialogData;

    init();

    function init() {
      $scope.viewTags = params.algorithm.viewTags;
      $scope.viewTagsMap = {};
      params.taskItem.viewTags.forEach(function(it) {
        $scope.viewTagsMap[it.viewTagId] = it.viewTagItemId;
      });
    }

    function getSelected() {
      var viewTags = [];

      Object.keys($scope.viewTagsMap).forEach(function(key) {
        if($scope.viewTagsMap[key] != null || $scope.viewTagsMap[key] != undefined) {
          viewTags.push({
            viewTagId: key,
            viewTagItemId: $scope.viewTagsMap[key]
          });
        }
      });

      return viewTags;
    }

    $scope.onSaveButtonClick = function() {
      var viewTags = getSelected();
      taskService.updateViewTags(params.taskItem.taskItemNo, {
          "items": viewTags
        })
        .then(function() {
          $scope.closeThisDialog({
            key: 'ok',
            result: {
              viewTags: viewTags
            }
          });
        });
    };
  }
]);