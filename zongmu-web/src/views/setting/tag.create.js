'use strict';

zongmu.controller("tagCreateController", ["$scope", "tagService", "enumService",
  function($scope, tagService, enumService) {

    var params = $scope.ngDialogData;

    initView();

    function initView() {
      $scope.controlTypes = enumService.getTagControlTypes();
      if(params && params.tag) {
        $scope.isEdit = true;
        $scope.type = params.tag.type;
        $scope.name = params.tag.name;
        $scope.algorithmId = params.tag.algorithmId;
      } else {
        $scope.type = "DROPDOWNBOX";
        $scope.algorithmId = params.algorithmId;
      }
    }

    $scope.onSaveButtonClick = function() {
      var data = {
        name: $scope.name,
        type: $scope.type,
        algorithmId: $scope.algorithmId
      };
      if(params && params.tag) {
        tagService.updateTag(params.tag.id, data)
          .then(function(res) {
            $scope.closeThisDialog({
              key: 'ok'
            });
          });
      } else {
        tagService.createTag(data)
          .then(function(res) {
            $scope.closeThisDialog({
              key: 'ok'
            });
          });
      }

    };
  }
]);