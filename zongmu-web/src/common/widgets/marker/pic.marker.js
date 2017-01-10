'use strict';

angular.module('huoyun-ui').directive("widgetsPicMarker", ["huoyunUtil", "$sce", "$log", function(huoyunUtil, $sce, $log) {
  return {
    restrict: "A",
    templateUrl: "widgets/marker/pic.marker.html",
    replace: true,
    scope: {
      "src": "=",
      "shapes": "=",
      "taskItemFile": "="
    },
    controller: "picMarkerController",
    link: function($scope, elem, attrs) {
      $scope.$watch("src", function() {
        if ($scope.src) {
          $log.debug("Picture Source URL:" + $scope.src);
          elem.find("img").attr("src", $sce.trustAsResourceUrl($scope.src));
        }
      });
    }
  }
}]);

angular.module('huoyun-ui').controller("picMarkerController", ["$scope", function($scope) {
  $scope.frameIndex = 1;
}]);