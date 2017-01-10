'use strict';

zongmu.controller("attrsDialogController", ['$scope', 'dialog', 'tagService',
  function($scope, dialog, tagService) {
    var params = $scope.ngDialogData;
    initData();

    function initData() {
      $scope.data = {
        color: params.shapeObj.color,
        attrs: {}
      };

      tagService.getTags()
        .then(function(res) {
          $scope.tags = res.data;
          initAttrs();
        });
    }

    $scope.onOkClick = function() {
      $scope.closeThisDialog({
        key: 'ok',
        result: [params.shapeObj, $scope.data.color, getAttrs()]
      });
    };

    function getAttrs() {
      var attrs = [];
      angular.forEach($scope.tags, function(tag, index) {
        var select = $scope.data.attrs[tag.name];
        if (select) {
          attrs.push({
            id: tag.id,
            name: tag.name,
            valueId: select.id,
            value: select.name
          });
        }
      });

      return attrs;
    }

    function initAttrs() {
      angular.forEach(params.shapeObj.attrs || [], function(attr, index) {
        $scope.data.attrs[attr.name] = {
          id: attr.valueId,
          value: attr.name
        };
      })
    }
  }
]);