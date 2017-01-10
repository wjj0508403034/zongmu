'use strict';

angular.module('huoyun-ui').directive("widgetsCheckbox", ["huoyunUtil", function(huoyunUtil) {
  return {
    restrict: "A",
    templateUrl: "widgets/checkbox/checkbox.html",
    replace: true,
    scope: {
      "ngModel": "=",
      "content": "@",
      "onChecked": "&",
      "onUnchecked": "&"
    },
    controller: "checkboxController",
    link: function($scope, elem, attrs) {

      $scope.$watch("ngModel", function(newValue) {
        huoyunUtil.toggleClass(elem, newValue, "checked");
      });

      $scope.onCheckboxClicked = function() {
        var disabled = elem.attr("disabled") === "disabled";
        if(!disabled) {
          $scope.ngModel = !$scope.ngModel;
          if($scope.ngModel) {
            $scope.onChecked();
          } else {
            $scope.onUnchecked();
          }

          $scope.$emit("changed");
        }
      };

    }
  };
}]);

angular.module('huoyun-ui').controller("checkboxController", ["$scope", function($scope) {

}]);