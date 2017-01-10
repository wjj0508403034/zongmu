'use strict';

/*
 * size: small, normal, large, huge
 */
angular.module('huoyun-ui').directive("widgetsButton", function() {
  return {
    restrict: "A",
    templateUrl: "widgets/button/button.html",
    replace: true,
    scope: {
      text: "@"
    },
    controller: "buttonController",
    link: function($scope, elem, attrs) {
      $scope.size = attrs.size;
    }
  };
});

angular.module('huoyun-ui').controller("buttonController", ["$scope", function($scope) {

}]);