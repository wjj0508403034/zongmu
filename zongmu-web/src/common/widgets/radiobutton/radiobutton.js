'use strict';

angular.module('huoyun-ui').directive("widgetsRadioButton", ["huoyunUtil", function(huoyunUtil) {
  return {
    restrict: "A",
    templateUrl: "widgets/radiobutton/radiobutton.html",
    replace: true,
    scope: {
      "ngModel": "=",
      "content": "@"
    },
    controller: "radioButtonController",
    link: function($scope, elem, attrs) {

      $scope.$watch("ngModel", function(newValue) {
        huoyunUtil.toggleClass(elem, newValue, "checked");
      });

    }
  };
}]);

angular.module('huoyun-ui').controller("radioButtonController", ["$scope", function($scope) {

  $scope.onRadioButtonClicked = function() {
    $scope.ngModel = !$scope.ngModel;
    $scope.$emit("changed");
  };
}]);