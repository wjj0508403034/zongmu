'use strict';

zongmu.controller("assetTagUpdateController", ['$scope', 'dialog', 'assetService', 'taskService',
  function($scope, dialog, assetService, taskService) {
    var params = $scope.ngDialogData;
    var weatherTagId = null;
    var roadTagId = null;
    init();

    function init() {
      if(params.asset) {
        weatherTagId = params.asset.weatherTagId;
        roadTagId = params.asset.roadTagId;
      }

      if(params.taskItem) {
        weatherTagId = params.taskItem.weatherTagId;
        roadTagId = params.taskItem.roadTagId;
      }

      assetService.getAssetTags()
        .then(function(tags) {
          initTags(tags);
        });
    }

    function initTags(tags) {
      $scope.weatherTags = [];
      $scope.roadTags = [];
      tags.forEach(function(it) {
        if(it.category === 'ROAD') {
          if(roadTagId === it.id) {
            $scope.roadTag = it;
          }
          $scope.roadTags.push(it);
        } else if(it.category === 'WEATHER') {
          if(weatherTagId === it.id) {
            $scope.weatherTag = it;
          }
          $scope.weatherTags.push(it);
        }
      });
    }

    $scope.onSaveButtonClick = function() {
      if(params.asset) {
        assetService.updateAssetTags(params.asset.assetNo, {
            weatherTagId: $scope.weatherTag.id,
            roadTagId: $scope.roadTag.id
          })
          .then(function() {
            $scope.closeThisDialog({
              key: 'ok'
            });
          });
      } else if(params.taskItem) {
        taskService.updateAssetTaskTags(params.taskItem.taskItemNo, {
            weatherTagId: $scope.weatherTag.id,
            roadTagId: $scope.roadTag.id
          })
          .then(function() {
            $scope.closeThisDialog({
              key: 'ok',
              result: {
                roadTag: $scope.roadTag,
                weatherTag: $scope.weatherTag
              }
            });
          });
      }

    };
  }
]);