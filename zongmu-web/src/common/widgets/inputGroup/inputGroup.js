'use strict';

angular.module('huoyun-ui').directive("widgetsInputGroup", ["huoyunUtil", function(huoyunUtil) {
  return {
    restrict: "A",
    templateUrl: "widgets/inputGroup/inputGroup.html",
    replace: true,
    scope: {
      "label": "@",
      "ngModel": "=",
      "type": "@",
      "readonly": "@",
      "requried": "@",
      "hasError": "@",
      "disabled": "@"
    },
    controller: "inputGroupController",
    controllerAs: "inputGroup",
    link: function($scope, elem, attrs) {

      $scope.$watch("requried", function(newValue) {
        huoyunUtil.toggleClass(elem, huoyunUtil.boolParse(newValue), "requried");
      });

      $scope.$watch("readonly", function(newValue) {
        huoyunUtil.toggleClass(elem, huoyunUtil.boolParse(newValue), "readonly");
      });

      $scope.$watch("error", function(newValue) {
        huoyunUtil.toggleClass(elem, huoyunUtil.boolParse(newValue), "error");
      });
    }
  };
}]);

angular.module('huoyun-ui').controller("inputGroupController", ["$scope", "huoyunUtil", function($scope, huoyunUtil) {

}]);