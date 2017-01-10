'use strict';

angular.module('huoyun-ui').directive("widgetsMarkInfoBar", function() {
  return {
    restrict: "A",
    templateUrl: "widgets/shape/mark.info.bar.html",
    replace: true,
    scope: {
      markInfo: "=",
      colorGroup: "=",
      tags: "=",
      onColorChanged: "&"
    },
    controller: "markInfoBarController",
    link: function($scope, elem, attrs) {

    }
  };
});

angular.module('huoyun-ui').controller("markInfoBarController", ["$scope", function($scope) {

  $scope.onColorSelectedChanged = function(colorTag) {
    $scope.onColorChanged({
      colorTag: colorTag
    });
    if($scope.markInfo && $scope.markInfo.color !== colorTag.color) {
      $scope.markInfo.changeColor(colorTag.color);
    }
  }
}]);