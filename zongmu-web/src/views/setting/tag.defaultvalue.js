'use strict';

zongmu.controller("tagDefaultValueController", ["$scope", "tagService", "dialog",
  function($scope, tagService, dialog) {
    var params = $scope.ngDialogData;
    initView();

    function initView() {
      $scope.tag = angular.copy(params.tag);
      $scope.selectTagItem = null;
      if ($scope.tag.type === 'DROPDOWNBOX' || $scope.tag.type === 'RADIOBUTTON') {
        var results = $scope.tag.items.filter(function(it) {
          return it.default;
        });

        if (results.length > 0) {
          $scope.selectTagItem = results[0];
        }
      }
    }

    $scope.onSaveButtonClick = function() {
      var tagItems = [];
      if ($scope.tag.type === 'DROPDOWNBOX' || $scope.tag.type === 'RADIOBUTTON') {
        if (!$scope.selectTagItem) {
          dialog.showError("请选择一个默认值。");
          return;
        } else {
          tagItems.push($scope.selectTagItem);
        }
      } else if ($scope.tag.type === 'CHECKBOX') {
        angular.forEach($scope.tag.items, function(item, index) {
          if (item.default) {
            tagItems.push(item);
          }
        });
      }

      if (tagItems.length > 0) {
        tagService.setMultiDefaultValues($scope.tag.id, tagItems)
          .then(function(res) {
            $scope.closeThisDialog({
              key: 'ok'
            });
          });
      }
    };
  }
]);